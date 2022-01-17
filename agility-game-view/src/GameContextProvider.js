import React, {useState} from 'react'

const GameContext = React.createContext(null)

const GameContextProvider = (props) => {

    const [playing, setPlaying] = useState(false)

    return (
        <GameContext.Provider value={{playing: playing, setPlaying: setPlaying}}>
            {props.children}
        </GameContext.Provider>
    )
}

export default GameContextProvider
export {GameContext}