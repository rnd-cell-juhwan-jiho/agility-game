import React, {useState, useEffect, useContext} from 'react'
import GameStatus from './GameStatus'
import {AuthContext} from '../AuthProvider'
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
    const [losers, setLosers] = useState([])
    const [status, setStatus] = useState(GameStatus.VOTING)
    const [count, setCount] = useState(-1)  //countdown

    const [bidTimer$, _] = useState(timer(1000))
    const [timerSubs, setTimerSubs] = useState(null)
    const [lastBid, setLastBid] = useState(0)
    const [submitBid, setSubmitBid] = useState(1)

    useEffect(() => {
        if(webSocket !== null)
            return

        let url = "ws://localhost:8080/game?id=" + gameId
        const ws = new WebSocket(url)

        ws.onopen = e => {
            console.log("onopen()..")
            setWebSocket(ws)
        }

        ws.onmessage = e => {
            console.log("onmessage()..")
            let msg = JSON.parse(e.data)
            
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
    })

    //reset game if game cancels at GameStatus.RUNNING
    useEffect(() => {
        if(status !== GameStatus.VOTING)
            return

        setCount(-1)
        setLastBid(0)
        setSubmitBid(1)
    }, [status])

    const handleInit = (msg) => {
        setUsers(msg.users)
    }

    const handleUserIn = (msg) => {
        setUsers(prev => [...prev, {username: msg.username, ready: false}])
    }

    const handleUserOut = (msg) => {
        setUsers(prev => prev.filter(user => user.username !== msg.username))
    }

    const handleReady = (msg) => {
        setUsers(prev => prev.map(user => {
            if(user === msg.username)
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

    const sendSubmitBid = (e) => {
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
            <div className="Game">
                <div className="Bid">
                    <div>last bid: {lastBid}</div>
                    <div>next bid: {submitBid}</div>
                    <button onClick={sendSubmitBid}>Submit Next Bid</button>
                </div>
                <div className="ChipsContainer">
                    {status === GameStatus.COUNTDOWN && 
                        <div className="Countdown">{count}</div>
                    }
                    {users.map((user, idx) => <div className="Chip" key={idx}>{user.username}</div>)}
                </div>
            </div>
            <UserList users={users}/>
            <LoserList losers={losers}/>
        </div>
    )
}

export default Game