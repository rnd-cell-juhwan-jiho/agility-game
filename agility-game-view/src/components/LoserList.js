import React from 'react'

const LoserList = (props) => {
    return (
        <div className="LoserList">
            {props.losers.length && props.losers.map(username => <div className="Loser">{username}</div>)}
        </div>
    )
}

export default LoserList
