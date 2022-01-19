import './App.css'
import {BrowserRouter as Router, Route, Routes, Navigate} from 'react-router-dom'
import Header from './components/header/Header'
import Login from './components/login/Login'
import Games from './components/Games'
import Game from './components/game/Game'
import GameContextProvider from './GameContextProvider'
import AuthProvider from './AuthProvider'
import RequireAuth from './RequireAuth'
import {useEffect} from 'react'

function App() {

  useEffect(() => {
    return ()=>console.log("unmounting..")
  })

  return (
    <AuthProvider><GameContextProvider>

      <div className="App">
        <Header/>
        <Router>
          <Routes>
            <Route path="/" element={<Navigate to="/games"/>}/>
            <Route path="/login" element={<Login/>}/>
            <Route path="/games" element={
              <RequireAuth>
                <Games/>
              </RequireAuth>
            }/>
            <Route path="/game/:gameId" element={
              <RequireAuth>
                  <Game/>
              </RequireAuth>
            }/>
          </Routes>
        </Router>
      </div>

    </GameContextProvider></AuthProvider>
  );
}

export default App;
