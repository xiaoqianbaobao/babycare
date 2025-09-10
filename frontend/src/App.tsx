import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { Layout } from 'antd'
import MainLayout from './components/Layout/MainLayout'
import Login from './pages/Auth/Login'
import Register from './pages/Auth/Register'
import Dashboard from './pages/Dashboard/Dashboard'
import GrowthRecord from './pages/GrowthRecord/GrowthRecord'
import AIParenting from './pages/AIParenting/AIParenting'
import EducationPlanning from './pages/EducationPlanning/EducationPlanning'
import FamilyCollaboration from './pages/FamilyCollaboration/FamilyCollaboration'
import FamilyManagement from './pages/FamilyManagement/FamilyManagement'
import Profile from './pages/Profile/Profile'
import { useAuthStore } from './stores/authStore'

function App() {
  const { user } = useAuthStore()

  // 受保护的路由组件
  const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
    return user ? <MainLayout>{children}</MainLayout> : <Navigate to="/login" />
  }

  return (
    <Router>
      <Layout style={{ minHeight: '100vh' }}>
        <Routes>
          {/* 公开路由 */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          {/* 受保护的路由 */}
          <Route path="/" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } />
          
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } />
          
          <Route path="/growth-record" element={
            <ProtectedRoute>
              <GrowthRecord />
            </ProtectedRoute>
          } />
          
          <Route path="/ai-parenting" element={
            <ProtectedRoute>
              <AIParenting />
            </ProtectedRoute>
          } />
          
          <Route path="/education-planning" element={
            <ProtectedRoute>
              <EducationPlanning />
            </ProtectedRoute>
          } />
          
          <Route path="/family-collaboration" element={
            <ProtectedRoute>
              <FamilyCollaboration />
            </ProtectedRoute>
          } />
          
          <Route path="/family-management" element={
            <ProtectedRoute>
              <FamilyManagement />
            </ProtectedRoute>
          } />
          
          <Route path="/profile" element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          } />
          
          {/* 默认重定向 */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </Layout>
    </Router>
  )
}

export default App