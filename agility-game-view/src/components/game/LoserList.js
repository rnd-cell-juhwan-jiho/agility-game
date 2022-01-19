import React from 'react'

const LoserList = (props) => {
    return (
        <div className="LoserList">
            <div className="Guide">LOSERS</div>
            {props.losers.map((username, idx) => 
                <div className="Loser" key={idx}>{username}</div>
            )}
        </div>
    )
}

export default LoserList