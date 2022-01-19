import React from 'react'

const LoserList = (props) => {
    return (
        <div className="LoserList">
            <div className="Guide">Losers</div>
            {props.losers.length !== 0 && 
                props.losers.map((username, idx) => 
                    <div className="Loser" key={idx}>{username}</div>
                )
            }
        </div>
    )
}

export default LoserList