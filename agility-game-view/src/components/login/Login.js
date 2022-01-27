import React, {useContext} from 'react'
import { AuthContext } from '../contexts/AuthProvider'
import {Navigate} from 'react-router-dom'
import './Login.css'

const Login = (props) => {

    const {username, setUsername} = useContext(AuthContext)

    const handleSubmit = (e) => {
        e.preventDefault()
    
        let input = e.currentTarget.username.value
        if(input === ""){
            alert("Username can't be nothing!")
            return
        }

        setUsername(input)
    }

    if(username !== null)
        return <Navigate to="/agility-game/games"/>

    return (
        <div className="Login">
            <div className="LoginContainer">
                <div className="Guide">Login to play!</div>
                <form onSubmit={handleSubmit}>
                    <input type="text" name="username" placeholder="username"/>
                    <br/>
                    <button type="submit">Login</button>
                </form>
            </div>
        </div>
    )
}

export default Login
