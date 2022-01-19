import React, {useState, useEffect, useContext} from 'react'
import GameStatus from './GameStatus'
import {AuthContext} from '../../AuthProvider'
import {useParams, useNavigate} from 'react-router-dom'
import './Game.css'
import MessageType from './MessageType'
import {timer} from 'rxjs'
import UserList from './UserList'
import LoserList from './LoserList'

const Game = () => {

    const navigate = useNavigate()
    const {gameId} = useParams()
    const {username} = useContext(AuthContext)
    const [webSocket, setWebSocket] = useState(null)
    const [users, setUsers] = useState([])
    const [nextReady, setNextReady] = useState(true)
    const [losers, setLosers] = useState([])
    const [status, setStatus] = useState(GameStatus.VOTING)
    const [count, setCount] = useState(-1)  //countdown
    const [lost, setLost] = useState(false)

    const [bidTimer$, _] = useState(timer(1000))
    const [timerSubs, setTimerSubs] = useState(null)
    const [lastBid, setLastBid] = useState(0)
    const [submitBid, setSubmitBid] = useState(1)

    useEffect(() => {
        let cleanup = () => {
            console.log("in cleanup..")
            if(webSocket === null)
                return

            console.log("closing webSocket..")
            webSocket.close()
        }
        return cleanup
    }, [])

    useEffect(() => {
        if(webSocket !== null)
            return

        let url = "ws://localhost:8080/game?game-id=" + gameId + "&username=" + username
        const ws = new WebSocket(url)

        ws.onopen = _ => {
            console.log("onopen()..")
            setWebSocket(ws)
            
            let init = JSON.stringify({
                type: MessageType.USER_IN,
                username: username
            })
            ws.send(init)
        }

        ws.onmessage = e => {
            console.log("onmessage()..")
            let msg = JSON.parse(e.data)
            console.log(msg)
            
            switch(msg.type){
                case MessageType.INIT:
                    handleInit(msg)
                    break
                case MessageType.USER_IN:
                    handleUserIn(msg)
                    break
                case MessageType.USER_OUT:
                    handleUserOut(msg)
                    break
                case MessageType.READY:
                    handleReady(msg)
                    break
                case MessageType.COUNTDONW:
                    handleCountdown(msg)
                    break
                case MessageType.BID:
                    handleBid(msg)
                    break
                case MessageType.END:
                    handleEnd(msg)
                    break
            }
        }

        ws.onclose = e => {
            console.log(e)
            navigate(-1)
        }

        ws.onerror = err => {
            console.log(err.message)
            navigate(-1)
        }

    }, [])

    //reset game if game cancels at GameStatus.RUNNING
    useEffect(() => {
        if(status !== GameStatus.VOTING)
            return

        setCount(-1)
        setLastBid(0)
        setSubmitBid(1)
    }, [status])

    const handleInit = (msg) => {
        setUsers(prev => [...prev, ...msg.users])
    }

    const handleUserIn = (msg) => {
        setUsers(prev => [...prev, {username: msg.username, ready: false}])
    }

    const handleUserOut = (msg) => {
        setUsers(prev => prev.filter(user => user.username !== msg.username))
    }

    const handleReady = (msg) => {
        if(msg.username === username)
            setNextReady(!msg.ready)

        setUsers(prev => prev.map(user => {
            
            if(user.username === msg.username)
                return {username: msg.username, ready: msg.ready}
            else
                return user
        }))
    }

    const handleCountdown = (msg) => {
        setCount(msg.count)
        if(msg.count === -1)
            setStatus(GameStatus.VOTING)
        else if(msg.count === 0)
            setStatus(GameStatus.RUNNING)
        else
            setStatus(GameStatus.COUNTDOWN)
    }

    const handleBid = (msg) => {
        if(msg.bid >= submitBid)
            setSubmitBid(msg.bid)
        setLastBid(msg.bid)

        if(timerSubs !== null){
            timerSubs.unsubscribe()
            setTimerSubs(null)
        }

        setTimerSubs(bidTimer$.subscribe({
            next: _ => {},
            error: err => console.log(err),
            complete: () => {
                setSubmitBid(prev => prev+1)
                setTimerSubs(null)
            }
        }))
    }

    const handleEnd = (msg) => {
        if(msg.cancel)
            handleCancel()

        setLosers(msg.losers)
        if(msg.terminating){
            if(status !== GameStatus.TERMINATING)
                setStatus(GameStatus.TERMINATING)
        }
        else{
            if(status !== GameStatus.ENDING)
                setStatus(GameStatus.ENDING)
            
        }
    }

    const handleCancel = () => {
        setStatus(GameStatus.VOTING)
    }

    const sendReady = () => {
        if(status !== GameStatus.VOTING && status !== GameStatus.COUNTDOWN)
            return
            
        let outbound = JSON.stringify({
            type: MessageType.READY,
            username: username,
            ready: nextReady
        })
        webSocket.send(outbound)
    }

    const sendNextBid = (e) => {
        e.preventDefault()

        if(status !== GameStatus.RUNNING && status !== GameStatus.ENDING)
            return

        //1) submit submitBid
        webSocket.send(JSON.stringify({
            type: MessageType.BID,
            username: username,
            bid: submitBid
        }))

        //2) increment submitBid
        setSubmitBid(prev => prev+1)
    }

    return (
        <div className="GameContainer">
            <div className="GameLeftContainer">
                { (status === GameStatus.VOTING || status === GameStatus.COUNTDOWN) &&
                    <button className="ReadyButton" onClick={sendReady}>
                        {nextReady ? <span>Ready</span> : <span>Cancel Ready</span>}
                    </button>
                }
                <div className="GameRunning">
                    {(status !== GameStatus.RUNNING && status !== GameStatus.ENDING) && <div className="Curtain"/>}
                    <div className="Bid">
                        <div>Last Bid: {lastBid}</div>
                        <div>Next Bid: {submitBid}</div>
                        <button onClick={sendNextBid}>Submit Next Bid</button>
                    </div>
                    <div className="ChipsContainer">
                        {status === GameStatus.COUNTDOWN && 
                            <div className="Countdown">{count}</div>
                        }
                        {users.map((user, idx) => <div className="Chip" key={idx}>{user.username}</div>)}
                    </div>
                </div>
            </div>
            <div className="GameRightContainer">
                <UserList users={users}/>
                <LoserList losers={losers}/>
            </div>
        </div>
    )
}

export default Game