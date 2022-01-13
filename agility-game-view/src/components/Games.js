import {useState, useEffect} from 'react'

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

    return (
        <div className='Games'>
            <p>This is games list.</p>
            <button onClick={fetchGames}>Fetch Games</button>
            {
                games.map((game, index) =>
                    <p className="Game" key={index}>{game}</p>
                )
            }
        </div>
    )
}

export default Games
