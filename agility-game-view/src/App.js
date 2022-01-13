import './App.css'
import {BrowserRouter as Router, Route, Routes, Navigate} from 'react-router-dom'
import Header from './components/Header'
import Login from './components/Login'
import Games from './components/Games'
import AuthProvider from './AuthProvider'
import RequireAuth from './RequireAuth'
// import {useContext} from 'react'
// import {AuthContext} from './AuthProvider'

function App() {

  console.log("App()..")

  // const {username} = useContext(AuthContext)

  return (
    <AuthProvider>
      <div className="App">
        <Header/>
        <hr/>
        <Router>
          <Routes>
            <Route path="/" element={<Navigate to="/games"/>}/>
            <Route path="/login" element={<Login/>}/>
            <Route path="/games" element={
              <RequireAuth>
                <Games/>
              </RequireAuth>
            }/>
          </Routes>
        </Router>
      </div>
    </AuthProvider>
  );
}

export default App;
