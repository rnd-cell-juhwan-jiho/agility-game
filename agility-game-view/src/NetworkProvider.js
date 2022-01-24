import React, { useState } from 'react'

const NetworkContext = React.createContext(null)

const NetworkProvider = (props) => {

    const [webSocket, setWebSocket] = useState(null)

    return (
        <NetworkContext.Provider value = {{webSocket, setWebSocket}}>
            {props.children}
        </NetworkContext.Provider>
    )
}

export default NetworkProvider
export {NetworkContext}