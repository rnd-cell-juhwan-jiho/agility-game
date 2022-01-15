import React from 'react'

const GameThumbnail = (props) => {

    const handleClick = (e) => {
        console.log(e);

        console.log("handleClick()...")
    }

    return (
        <div className="GameThumbnail" onClick={handleClick}>
            Game id:{props.game.game_id}, status:{props.game.status}, size:{props.game.size}
        </div>
    )
}

export default GameThumbnail