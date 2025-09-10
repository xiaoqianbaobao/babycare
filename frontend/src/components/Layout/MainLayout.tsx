import React, { useState } from 'react'
import { Layout, Menu, Avatar, Dropdown, Space, Badge, Button, Drawer } from 'antd'
import type { MenuProps } from 'antd'
import {
  HomeOutlined,
  CameraOutlined,
  RobotOutlined,
  BookOutlined,
  TeamOutlined,
  UserOutlined,
  BellOutlined,
  MenuOutlined,
  LogoutOutlined,
  SettingOutlined,
  UsergroupAddOutlined,
} from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '../../stores/authStore'
import { useFamilyStore } from '../../stores/familyStore'
import './MainLayout.css'

const { Header, Sider, Content } = Layout

interface MainLayoutProps {
  children: React.ReactNode
}

const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
  const navigate = useNavigate()
  const location = useLocation()
  const { user, logout } = useAuthStore()
  const { currentFamily, currentBaby } = useFamilyStore()
  const [collapsed, setCollapsed] = useState(false)
  const [mobileMenuVisible, setMobileMenuVisible] = useState(false)

  // 导航菜单项
  const menuItems: MenuProps['items'] = [
    {
      key: '/dashboard',
      icon: <HomeOutlined />,
      label: '首页',
    },
    {
      key: '/growth-record',
      icon: <CameraOutlined />,
      label: '成长记录',
    },
    {
      key: '/ai-parenting',
      icon: <RobotOutlined />,
      label: 'AI育儿',
    },
    {
      key: '/education-planning',
      icon: <BookOutlined />,
      label: '教育规划',
    },
    {
      key: '/family-collaboration',
      icon: <TeamOutlined />,
      label: '家庭协作',
    },
    {
      key: '/family-management',
      icon: <UsergroupAddOutlined />,
      label: '家庭管理',
    },
  ]

  // 用户下拉菜单
  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人资料',
      onClick: () => navigate('/profile'),
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '设置',
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: () => {
        logout()
        navigate('/login')
      },
    },
  ]

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key)
    setMobileMenuVisible(false)
  }

  return (
    <Layout className="main-layout">
      {/* 桌面端侧边栏 */}
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        className="layout-sider desktop-only"
        theme="light"
        width={240}
      >
        <div className="logo">
          <img src="/logo.svg" alt="慧成长" />
          {!collapsed && <span>慧成长</span>}
        </div>
        
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={handleMenuClick}
          className="nav-menu"
        />
      </Sider>

      <Layout>
        {/* 顶部导航栏 */}
        <Header className="layout-header">
          <div className="header-left">
            {/* 桌面端折叠按钮 */}
            <Button
              type="text"
              icon={<MenuOutlined />}
              onClick={() => setCollapsed(!collapsed)}
              className="desktop-only collapse-btn"
            />
            
            {/* 移动端菜单按钮 */}
            <Button
              type="text"
              icon={<MenuOutlined />}
              onClick={() => setMobileMenuVisible(true)}
              className="mobile-only"
            />

            {/* 当前家庭和宝宝信息 */}
            <div className="family-info">
              {currentBaby && (
                <Space>
                  <Avatar src={currentBaby.avatar} size="small">
                    {currentBaby.name[0]}
                  </Avatar>
                  <span className="baby-name">{currentBaby.name}</span>
                </Space>
              )}
            </div>
          </div>

          <div className="header-right">
            <Space size="large">
              {/* 通知 */}
              <Badge count={5} size="small">
                <Button type="text" icon={<BellOutlined />} />
              </Badge>

              {/* 用户信息 */}
              <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
                <div className="user-info">
                  <Avatar src={user?.avatar} size="small">
                    {user?.nickname?.[0] || user?.username?.[0]}
                  </Avatar>
                  <span className="user-name desktop-only">
                    {user?.nickname || user?.username}
                  </span>
                </div>
              </Dropdown>
            </Space>
          </div>
        </Header>

        {/* 主内容区域 */}
        <Content className="layout-content">
          {children}
        </Content>
      </Layout>

      {/* 移动端抽屉菜单 */}
      <Drawer
        title="菜单"
        placement="left"
        onClose={() => setMobileMenuVisible(false)}
        open={mobileMenuVisible}
        className="mobile-menu"
        width={280}
      >
        <div className="mobile-logo">
          <img src="/logo.svg" alt="慧成长" />
          <span>慧成长</span>
        </div>
        
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={handleMenuClick}
          className="mobile-nav-menu"
        />
      </Drawer>
    </Layout>
  )
}

export default MainLayout