import {useState, useEffect} from 'react'
import { useNavigate } from 'react-router-dom'
import GameThumbnail from './GameThumbnail'
import './Games.css'

// const test = [{
//     game_id: "123",
//     status: "VOTING",
//     size: 0
// }]

const Games = (props) => {

    const navigate = useNavigate()
    const [initialized, setInitialized] = useState(false)
    const [games, setGames] = useState([])
    const url = "http://localhost:8080/games"

    useEffect(() => {
        if(!initialized){
            setInitialized(true)
            fetchGames()
        }
    })

    const fetchGames = (e) => {
        fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(response => response.json())
        .then(data => {
            console.log(data)
            setGames(data.games)
        }).catch(error => {
            console.log("=== ERROR ===")
            console.log(error)
        })
    }

    const handleSubmit = e => {
        navigate("/game/"+e.currentTarget.gameIdInput.value)
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
                        && games.map((game, index) => <GameThumbnail key={index} game={game}/>)
                    }
                </tbody>
            </table>
            {games.length === 0 && <div className="EmptyGuide">(There are no games right now)</div>}
        </div>
    )
}

export default Games