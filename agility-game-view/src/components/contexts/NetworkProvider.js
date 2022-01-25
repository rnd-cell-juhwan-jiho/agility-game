import React, { useState, useEffect} from 'react'
import { useLocation } from 'react-router-dom'

const NetworkContext = React.createContext(null)

const NetworkProvider = (props) => {

    const [webSocket, setWebSocket] = useState(null)
    const location = useLocation()

    useEffect(() => {
        let path = location.pathname.split('/')[1]
        if(path === "game")
            return
        else if(webSocket !== null && webSocket !== undefined){
            if(webSocket.readyState !== 2 && webSocket.readyState !==3)
                webSocket.close()
            setWebSocket(null)
        }
    }, [location.pathname])

    return (
        <NetworkContext.Provider value = {{webSocket, setWebSocket}}>
            {props.children}
        </NetworkContext.Provider>
    )
}

export default NetworkProvider
export {NetworkContext}