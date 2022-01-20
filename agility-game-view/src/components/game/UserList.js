import React from 'react'

const UserList = (props) => {
    return (
        <div className="UserList">
            <div className="Guide">PLAYERS</div>
            {props.users.length && props.users.map((user, index) => 
                <div className="User" key={index}>
                    <span>{user.username}</span>
                    {user.ready && <span className="ReadySpan">READY</span>}
                </div>
            )}
        </div>
    )
}

export default UserList
