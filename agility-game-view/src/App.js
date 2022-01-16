import './App.css'
import {BrowserRouter as Router, Route, Routes, Navigate} from 'react-router-dom'
import Header from './components/Header'
import Login from './components/Login'
import Games from './components/Games'
import AuthProvider from './AuthProvider'
import RequireAuth from './RequireAuth'
import Game from './components/Game'

function App() {

  return (
    <AuthProvider>
      <div className="App">
        <Header/>
        <hr/>
        <Router>
          <Routes>
            <Route path="/" element={<Navigate to="/login"/>}/>
            <Route path="/login" element={<Login/>}/>
            <Route path="/games" element={
              <RequireAuth>
                <Games/>
              </RequireAuth>
            }/>
            <Route path="/game" element={
              <RequireAuth>
                <Game/>
              </RequireAuth>
            }/>
          </Routes>
        </Router>
      </div>
    </AuthProvider>
  );
}

export default App;
