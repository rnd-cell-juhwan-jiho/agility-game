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
    const [games, setGames] = useState([])
    const url = "http://localhost:8080/games"

    useEffect(() => {
        fetchGames()
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
        <div className='Games'>
            <p>This is games list.</p>
            <button onClick={fetchGames}>Update Games List</button>
            <form onSubmit={handleSubmit}>
                <input type="text" name="gameIdInput"/>
                <input type="submit" value="Join/Create Game"/>
            </form>
            {games && games.map((game, index) =>
                <GameThumbnail key={index} game={game}/>
            )}
        </div>
    )
}

export default Games