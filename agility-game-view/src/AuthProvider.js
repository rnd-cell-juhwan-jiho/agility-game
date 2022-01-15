import React, {useState} from 'react'

const AuthContext = React.createContext(null)  

const AuthProvider = (props) => {

    const [username, setUsername] = useState(null)

    return (
        <AuthContext.Provider value={{username: username, setUsername: setUsername}}>
            {props.children}
        </AuthContext.Provider>
    )
}

export default AuthProvider
export {AuthContext}