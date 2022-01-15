import React, {useState, useEffect, useContext} from 'react'
import GameStatus from './GameStatus'
import {AuthContext} from '../AuthProvider'
import {useParams} from 'react-router-dom'
import './Game.css'
import MessageType from './MessageType'
import {timer} from 'rxjs'

const Game = () => {

    const {gameId} = useParams()
    const {username} = useContext(AuthContext)
    const [webSocket, setWebSocket] = useState(null)
    const [users, setUsers] = useState([])
    const [status, setStatus] = useState(GameStatus.VOTING)
    const [count, setCount] = useState(-1)  //countdown

    const [bidTimer$, _] = useState(timer(1000))
    const [timerSubs, setTimerSubs] = useState(null)
    const [currentBid, setCurrentBid] = useState(0)
    const [submitBid, setSubmitBid] = useState(1)

    useEffect(() => {
        if(ws !== null)
            return

        let url = "ws://localhost:8080/" + gameId
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
        }

        ws.onerror = err => {
            console.log(err.message)
        }
    })

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
        if(msg.bid >= submidBid)
            setSubmitBid(msg.bid)
        setCurrentBid(msg.bid)

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
        if(msg.terminating){
            if(status !== GameStatus.TERMINATING)
                setStatus(GameStatus.TERMINATING)
        }
    }

    const submitBid = (e) => {
        e.preventDefault()

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
                    <div>current bid: {currentBid}</div>
                    <div>next bid: {submitBid}</div>
                    <button onClick={submitBid}>Submit Next Bid</button>
                </div>
                <div className="ChipContainer">
                    {status === GameStatus.COUNTDOWN && 
                        <div className="Countdown">{count}</div>
                    }
                    {users.map((user, idx) => {
                        {<div className="Chip" key={idx}>{user.username}</div>}
                    })}
                </div>
            </div>
            <div className="UserList">
                {users.map((user, idx) => 
                    {<div className="User" key={idx}>{user}</div>}
                )}
            </div>
        </div>
    )
}

export default Game