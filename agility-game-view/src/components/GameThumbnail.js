import React from 'react'

const GameThumbnail = (props) => {

    const handleClick = (e) => {
        console.log(e);

        console.log("handleClick()...")
    }

    return (
        <div className="GameThumbnail" onClick={handleClick}>
            {props.game.game_id} {props.game.status} {props.game.size}
        </div>
    )
}

export default GameThumbnail