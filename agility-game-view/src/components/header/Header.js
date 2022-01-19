import React, {useContext} from 'react'
import {AuthContext} from '../../AuthProvider'
import './Header.css'

const Header = (props) => {

    const {username} = useContext(AuthContext)

    return (
        <div className="Header">
            {username
                ? <span>Welcome, {username}</span>
                : <span>You are not logged in</span>
            }
        </div>
    )
}

export default Header
