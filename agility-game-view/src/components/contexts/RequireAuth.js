import React, {useContext} from 'react'
import {Navigate} from 'react-router-dom'
import { AuthContext } from './AuthProvider'

const RequireAuth = (props) => {

    const {username} = useContext(AuthContext)

    return (
        <div>
        {username 
            ? <>{props.children}</>
            : <Navigate to="/login"/>
        }
        </div>
    )
}

export default RequireAuth
