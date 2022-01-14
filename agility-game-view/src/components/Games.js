import {useState, useEffect} from 'react'
import GameThumbnail from './GameThumbnail'
import './Games.css'

const test = [{
    "game_id": "123",
    "status": "VOTING",
    "size": 0
}]

const Games = (props) => {

    const [games, setGames] = useState([])
    const url = "http://localhost:8080/games"

    useEffect(() => {
        // fetchGames()
    })

    const fetchGames = (e) => {
        e.preventDefault()
        
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

    const fetchGamesTest = (e) => {
        e.preventDefault()

        setGames(() => [...games, ...test]);
    }

    return (
        <div className='Games'>
            <p>This is games list.</p>
            <button onClick={fetchGamesTest}>Update Games List</button>
            {
                games.map((game, index) =>
                    <GameThumbnail key={index} game={game}/>
                )
            }
        </div>
    )
}

export default Games