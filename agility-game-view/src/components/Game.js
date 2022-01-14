import React, {useState, useEffect} from 'react'
import {useLinkClickHandler, useParams} from 'react-router-dom'
import './Game.css'

const Game = () => {

    const {gameId} = useParams()
    const [ws, setWs] = useState(null)
    const [users, setUsers] = useState([])

    useEffect(() => {
        if(ws !== null)
            return

        let url = "ws://localhost:8080/" + gameId
        const webSocket = new WebSocket(url)

        ws.onopen = e => {
            setWs(webSocket)
        }

        ws.onmessage = e => {
            let msg = JSON.parse(e.data)
            
            switch(msg){
                case "USER_IN":
                    handleUserIn(msg)
                    break
                case "USER_OUT":
                    handleUserOut(msg)
                    break
                case "READY":
                    handleReady(msg)
                    break
                case "COUNTDOWN":
                    handleCountdown(msg)
                    break
                case "BID":
                    handleBid(msg)
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

    const handleUserIn = (msg) => {
        let users = []
        setUsers(msg.users)
    }

    const handleUserOut = (msg) => {
        setUsers(msg.users)
    }

    const handleReady = (msg) => {
        
    }

    const handleCountdown = (msg) => {

    }

    const handleBid = (msg) => {

    }

    return (
        <div className="GameContainer">
            <div className="Game">
                <div className="ChipContainer">
                {users.map((user, idx) => {
                    {<div className="Chip"></div>}
                })}
                </div>
            </div>
            <div className="UserList">
                {users.map((user, idx) => 
                    {<div className="User" key="idx">{user}</div>}
                )}
            </div>
        </div>
    )
}

export default Game