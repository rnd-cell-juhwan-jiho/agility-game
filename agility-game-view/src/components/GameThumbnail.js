import React from 'react'
import {useNavigate} from 'react-router-dom'
import GameStatus from './game/GameStatus'

const GameThumbnail = (props) => {

    const navigate = useNavigate()

    const handleClick = (e) => {
        console.log(e);
        
        props.tryJoinGame(props.game.game_id)
    }

    return (
        <tr className="GameThumbnail" onClick={handleClick}>
            <td>{props.game.game_id}</td>
            <td>{props.game.status}</td>
            <td>{props.game.size}</td>
        </tr>
    )
}

export default GameThumbnail