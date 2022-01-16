import React, {useContext} from 'react'
import {AuthContext} from '../AuthProvider'
import './Header.css'

const Header = (props) => {

    const {username} = useContext(AuthContext)

    return (
        <div className="Header">
            <p>HEADER</p>
            {username
                ? <p>Welcome, {username}</p>
                : <p>You are not logged in.</p>
            }
        </div>
    )
}

export default Header
