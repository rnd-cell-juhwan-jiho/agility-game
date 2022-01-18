import React from 'react'

const UserList = (props) => {
    return (
        <div className="UserList">
            <p>This is User List.</p>
            {props.users.length && props.users.map(user => 
                <div className={user.ready ? "UserReady" : "UserNotReady"}>
                    {user.username}
                </div>
            )}
        </div>
    )
}

export default UserList
