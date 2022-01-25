import './App.css'
import {BrowserRouter as Router, Route, Routes, Navigate} from 'react-router-dom'
import Header from './components/header/Header'
import Login from './components/login/Login'
import Games from './components/Games'
import Game from './components/game/Game'
import GameContextProvider from './components/contexts/GameContextProvider'
import AuthProvider from './components/contexts/AuthProvider'
import RequireAuth from './components/contexts/RequireAuth'
import NetworkProvider from './components/contexts/NetworkProvider'

function App() {

  return (
    <Router>

    <AuthProvider><GameContextProvider><NetworkProvider>

      <div className="App">
        <Header/>
        {/* <Router> */}
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
        {/* </Router> */}
      </div>

      </NetworkProvider></GameContextProvider></AuthProvider>

      </Router>
  );
}

export default App;
