import React, {useContext} from 'react'
import { AuthContext } from '../AuthProvider'
import {Navigate} from 'react-router-dom'

const Login = (props) => {

    const {username, setUsername} = useContext(AuthContext)

    const handleSubmit = (e) => {
        e.preventDefault()
    
        let input = e.currentTarget.username.value
        setUsername(input)
        console.log('submitting')
    }

    if(username !== null)
        return <Navigate to="/games"/>

    return (
        <div className="Login">
            <p>This is Login page.</p>
            <form onSubmit={handleSubmit}>
                <input type="text" name="username"/>
                <input type="submit" value="Login"/>
            </form>
        </div>
    )
}

export default Login
