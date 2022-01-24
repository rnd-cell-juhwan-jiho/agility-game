import React from 'react'
const GameThumbnail = (props) => {

    const handleClick = (e) => {
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