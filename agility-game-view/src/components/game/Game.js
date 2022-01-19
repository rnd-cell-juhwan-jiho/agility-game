import React, {useState, useEffect, useContext} from 'react'
import GameStatus from './GameStatus'
import {AuthContext} from '../../AuthProvider'
import {useParams, useNavigate} from 'react-router-dom'
import './Game.css'
import MessageType from './MessageType'
import {timer} from 'rxjs'
import UserList from './UserList'
import LoserList from './LoserList'
import Chip from './Chip'

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

    const [chips, setChips] = useState({})

    const [bidTimer$, _] = useState(timer(1000))
    const [timerSubs, setTimerSubs] = useState(null)
    const [lastBid, setLastBid] = useState(0)
    const [nextBid, setNextBid] = useState(1)

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
        setNextBid(1)
    }, [status])

    useEffect(() => {
        let newChips = {}
        users.forEach(user => newChips[user.username] = false)
        setChips(newChips)
    }, [users])

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
        if(msg.bid >= nextBid)
            setNextBid(msg.bid)
        setLastBid(msg.bid)

        animateChip(msg.username)

        if(timerSubs !== null){
            timerSubs.unsubscribe()
            setTimerSubs(null)
        }

        setTimerSubs(bidTimer$.subscribe({
            next: _ => {},
            error: err => console.log(err),
            complete: () => {
                setNextBid(prev => prev+1)
                setTimerSubs(null)
            }
        }))
    }

    const animateChip = (user) => {        
        let newChips = JSON.parse(JSON.stringify(chips))
        newChips[user] = true
        setChips(newChips)
    }

    const handleEnd = (msg) => {
        if(msg.cancel)
            handleCancel()

        setLosers(msg.losers)
        if(msg.losers.indexOf(username) !== -1)
            setLost(true)
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
            bid: nextBid
        }))

        //2) increment submitBid
        setNextBid(prev => prev+1)
    }

    return (
        <div className="Game">
            { (status === GameStatus.VOTING || status === GameStatus.COUNTDOWN) &&
                <button className="ReadyButton" onClick={sendReady}>
                    <span>{nextReady ? "READY" : "Cancel Ready"}</span>
                </button>
            }
            <div className="GameContainer">
                <div className="GameLeftContainer">
                    <div className="GameRunning">
                        {(
                            status === GameStatus.VOTING 
                            || status === GameStatus.COUNTDOWN
                            || (status === GameStatus.ENDING && lost)
                            || status === GameStatus.TERMINATING
                        ) &&
                            <div className="Curtain">
                                {status === GameStatus.TERMINATING && <div className="GameTerminatingGuide">GAME ENDED.</div>}
                                {lost && <div className="LostGuide">You Lost!</div>}
                                {status === GameStatus.COUNTDOWN && <div className="Countdown">{count}</div>}
                            </div>}
                        <div className="Bid">
                            <button onClick={sendNextBid}>Submit Next Bid</button>
                            <div className="LastBid">Last Bid: {lastBid}</div>
                            <div className="NextBid">Next Bid: {nextBid}</div>
                        </div>
                        <div className="ChipsContainer">
                            {users.map((user, idx) => <Chip key={idx} username={user.username} chips={chips} setChips={setChips}/>)}
                        </div>
                    </div>
                </div>
                <div className="GameRightContainer">
                    <UserList users={users}/>
                    {losers.length !== 0 && <LoserList losers={losers}/>}
                </div>
            </div>
        </div>
    )
}

export default Game