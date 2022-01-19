import React from 'react'

const UserList = (props) => {
    return (
        <div className="UserList">
            <div className="Guide">PLAYERS</div>
            {props.users.length && props.users.map((user, index) => 
                <div className={user.ready ? "UserReady" : "UserNotReady"} key={index}>
                    {!user.ready ? user.username : user.username+" (ready)"}
                </div>
            )}
        </div>
    )
}

export default UserList
