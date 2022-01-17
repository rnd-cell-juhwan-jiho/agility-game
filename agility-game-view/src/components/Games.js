import {useState, useEffect, useContext} from 'react'
import { useNavigate } from 'react-router-dom'
import GameThumbnail from './GameThumbnail'
import './Games.css'
import {GameContext} from '../GameContextProvider'

const test = [{
    game_id: "123",
    status: "VOTING",
    size: 0
}]

const Games = (props) => {

    const navigate = useNavigate()
    const [games, setGames] = useState([])
    const url = "http://localhost:8080/games"
    const {playing, setPlaying} = useContext(GameContext)

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

    const fetchGamesTest = e => {
        e.preventDefault()

        setGames(() => [...games, ...test]);
    }

    const handleJoin = e => {
        e.preventDefault()

        navigate("/game/"+e.currentTarget.gameIdInput.value)
    }

    return (
        <div className='Games'>
            <p>This is games list.</p>
            <button onClick={fetchGamesTest}>Update Games List</button>
            <form onSubmit={handleJoin}>
                <input type="text" name="gameIdInput"/>
                <input type="submit" value="Join/Create Game"/>
            </form>
            {games.map((game, index) =>
                <GameThumbnail key={index} game={game}/>
            )}
        </div>
    )
}

export default Games