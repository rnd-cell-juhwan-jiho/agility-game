import {useState, useEffect, useContext} from 'react'
import { useNavigate } from 'react-router-dom'
import GameThumbnail from './GameThumbnail'
import './Games.css'
import Spinner from './spinner/Spinner'
import NetStat from './NetStat'
import GameStatus from './game/GameStatus'
import Resources from '../Resources'
import { NetworkContext } from '../NetworkProvider'

const Games = (props) => {

    const {webSocket, setWebSocket} = useContext(NetworkContext)
    const navigate = useNavigate()
    const [games, setGames] = useState([])
    const [fetchStatus, setFetchStatus] = useState(NetStat.IDLE)
    const gamesUrl = "http://"+Resources.HOSTNAME+":"+Resources.PORT+"/games"
    const gameStatusUrl = "http://"+Resources.HOSTNAME+":"+Resources.PORT+"/game/status?game-id="

    useEffect(() => {
        fetchGames()

        if(webSocket !== null && webSocket !== undefined){
            console.log(typeof webSocket)
            if(webSocket.readyState !== 3)  //0:CONNECTING, 1:OPEN, 2:CLOSING, 3:CLOSED
                webSocket.close()
            setWebSocket(null)
        }
    }, [])

    const fetchGames = (e) => {
        setFetchStatus(NetStat.LOADING)

        fetch(gamesUrl, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(response => response.json())
        .then(data => {
            console.log(data)
            setGames(data.games)
            setFetchStatus(NetStat.IDLE)
        }).catch(error => {
            console.log("=== ERROR ===")
            console.log(error)
            setFetchStatus(NetStat.ERROR)
        })
    }

    const handleSubmit = e => {
        e.preventDefault()

        let gameId = e.currentTarget.gameIdInput.value
        if(gameId === ""){
            alert("Game ID can't be nothing!")
            return
        }

        tryJoinGame(gameId)
    }

    const tryJoinGame = gameId => {
        fetch(gameStatusUrl + gameId, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(response => {
            if(response.status === 404){
                alert("Game not found. Creating new game..")
                navigate("/game/"+gameId)
            }
            else{
                response.json().then(data => {
                    if(data.status === GameStatus.VOTING)
                        navigate("/game/"+gameId)
                    else
                        alert("You can only join games when it's VOTING.\n\nCurrent status: "+data.status)
                })
            }
        })
    }

    return (
        <div className="Games">
            <div className="NavBar">
                <button onClick={fetchGames} className="UpdateButton">Update Games</button>
                <form onSubmit={handleSubmit}>
                    <input type="text" name="gameIdInput" placeholder="Game ID"/><br/>
                    <button type="submit">Join/Create Game</button>
                </form>
            </div>
            <table className="Table">
                <thead>
                    <tr>
                        <th>Game ID</th>
                        <th>Status</th>
                        <th>Players #</th>
                    </tr>
                </thead>
                <tbody>
                    {games.length !== 0
                        && games.map((game, index) => 
                            <GameThumbnail key={index} game={game} tryJoinGame={tryJoinGame}/>
                        )
                    }
                </tbody>
            </table>
            {games.length === 0 && fetchStatus === NetStat.IDLE && <div className="EmptyGuide">(There are no games right now)</div>}
            <div className="NotIdleGuide">
                {fetchStatus === NetStat.LOADING && <Spinner/>}
                {fetchStatus === NetStat.ERROR && <div className="Error">Something went wrong :(</div>}
            </div>
        </div>
    )
}

export default Games