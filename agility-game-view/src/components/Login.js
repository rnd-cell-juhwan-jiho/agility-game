import React, {useContext} from 'react'
import { AuthContext } from '../AuthProvider'

const Login = (props) => {

    const {setUsername} = useContext(AuthContext)

    const handleSubmit = (e) => {
        e.preventDefault()
    
        let input = e.currentTarget.username.value
        setUsername(input)
        console.log('submitting')
    }

    return (
        <div className="Login">
            <p>This is Login page.</p>
            <form onSubmit={handleSubmit}>
                <input type="text" name="username"/>
                <input type="button" value="Login"/>
            </form>
        </div>
    )
}

export default Login
