import React, {useState, useEffect} from 'react'
import {useParams} from 'react-router-dom'
import './Game.css'

const Game = () => {

    const {gameId} = useParams()
    const [ws, setWs] = useState(null)
    const [users, setUsers] = useState([])
    const [usersReady, setUsersReady] = useState([])

    useEffect(() => {
        if(ws === null)
            return

        let url = "ws://localhost:8080/"+gameId
        setWs(new WebSocket(url))
        
    })

    return (
        <div className="GameContainer">
            <div className="Game">

            </div>
            <div className="UserList">
                
            </div>
        </div>
    )
}

export default Game