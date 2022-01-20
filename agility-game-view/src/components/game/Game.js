import React, {useState, useEffect, useContext} from 'react'
import GameStatus from './GameStatus'
import {AuthContext} from '../../AuthProvider'
import {useParams, useNavigate} from 'react-router-dom'
import './Game.css'
import MessageType from './MessageType'
import {Subject, of, interval, timer, concat} from 'rxjs'
import {take, map, switchMapTo} from 'rxjs/operators'
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

    const [bidEmitter$, _be] = useState(new Subject())
    const [autoBidCount, setAutoBidCount] = useState(-1)
    const [autoBidCount$, _abc] = useState(concat(
            of(10),
            interval(1000).pipe(take(10), map(t => 9-t))
        ))
    // const [autoBidSubs, setAutoBidSubs] = useState(null)

    const [bidTimer$, _bt] = useState(timer(600))
    const [bidTimerSubs, setBidTimerSubs] = useState(null)
    const [lastBid, setLastBid] = useState(0)
    const [nextBid, setNextBid] = useState(1)

    /* [ISSUE] webSocket is null at cleanup execution but connection is still open.. */

    // useEffect(() => {
    //     let cleanup = () => {
    //         console.log("in cleanup..")
    //         if(webSocket === null)
    //             return

    //         console.log("closing webSocket..")
    //         webSocket.close()
    //     }
    //     return cleanup
    // }, [])

    /* initalize webSocket */
    useEffect(() => {
        if(webSocket !== null)
            return

        let url = "ws://localhost:8080/game?game-id=" + gameId + "&username=" + username
        const ws = new WebSocket(url)

        ws.onopen = _ => {
            console.log("onopen()..")
            setWebSocket(ws)
            
            window.onpopstate = e => {
                console.log("window.onpopstate..")
                ws.close()
            }
            
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

    /* reset game if game cancels at GameStatus.RUNNING */
    useEffect(() => {
        if(status === GameStatus.VOTING){
            setCount(-1)
            setLastBid(0)
            setNextBid(1)
        }
        else if(status === GameStatus.RUNNING){
            bidEmitter$.asObservable().pipe(
                switchMapTo(autoBidCount$)
            ).subscribe({
                next: t => {
                    setAutoBidCount(t)
                    if(t == 0)
                        sendNextBid(null)
                }
            })

            bidEmitter$.next()
        }
        else if(status === GameStatus.TERMINATING){
            console.log("completing bidEmitter$...")
            bidEmitter$.complete()
        }
    }, [status])

    /* chip animation */
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
        else if(msg.count === 0){
            setStatus(GameStatus.RUNNING)
        }
        else
            setStatus(GameStatus.COUNTDOWN)
    }

    const handleBid = (msg) => {
        /* SET BID TIMER RULE HERE */
        if(msg.username === username)
            bidEmitter$.next();

        //1) set nextBid to the highest among incoming bids and current nextBid
        if(msg.bid >= nextBid)
            setNextBid(msg.bid)
        setLastBid(msg.bid)

        animateChip(msg.username)

        //2) if 1s bid timer is ongoing, cancel it
        if(bidTimerSubs !== null){
            bidTimerSubs.unsubscribe()
            setBidTimerSubs(null)
        }

        //3) restart bid timer
        setBidTimerSubs(bidTimer$.subscribe({
            next: _ => {},
            error: err => console.log(err),
            complete: () => {
                setNextBid(prev => prev+1)
                setBidTimerSubs(null)
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
        if(e !== null)
            e.preventDefault()

        //block if game is not RUNNING nor ENDING, or lost
        if(status !== GameStatus.RUNNING && status !== GameStatus.ENDING){
            alert("You can bid only when the game is ongoing.")
            return
        }
        else if (lost){
            alert("Can't bid when you've lost!")
            return
        }

        //1) submit nextBid
        webSocket.send(JSON.stringify({
            type: MessageType.BID,
            username: username,
            bid: nextBid
        }))

        //2) increment nextBid
        setNextBid(prev => prev+1)
    }

    // const resetAutoBid = () => {
    //     if(autoBidSubs !== null){
    //         console.log("unsubscribing..")
    //         autoBidSubs.unsubscribe()
    //     }
    //     setAutoBidSubs(
    //         autoBidCount$.subscribe({
    //             next: t => setAutoBidCount(t),
    //             error: err => console.log(err),
    //             complete: () => sendNextBid()
    //         })
    //     )
    // }

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
                            </div>
                        }
                        <div className="Bid">
                            <button onClick={sendNextBid}>Submit Next Bid</button>

                            {(status === GameStatus.RUNNING || status === GameStatus.ENDING)
                                && <div className="AutoBidCount">engaging auto-bid in: <span>{autoBidCount}</span>...</div>
                            }
                            <br/>
                            <div className="LastBid"> Last Bid: {lastBid}</div>
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