import React, { useEffect } from 'react'
import { Form, Input, Button, Card, Typography, Divider, Space, message } from 'antd'
import { UserOutlined, LockOutlined, WechatOutlined } from '@ant-design/icons'
import { Link, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../../stores/authStore'
import { LoginForm } from '../../types'
import './Auth.css'

const { Title, Text } = Typography

// 本地存储的键名
const STORAGE_KEY = 'babycare_login_memory'

interface LoginMemory {
  emailOrUsername?: string
  lastUsed?: string
}

const Login: React.FC = () => {
  const navigate = useNavigate()
  const { login, isLoading } = useAuthStore()
  const [form] = Form.useForm()

  // 加载记忆的数据
  useEffect(() => {
    const savedData = localStorage.getItem(STORAGE_KEY)
    if (savedData) {
      try {
        const memory: LoginMemory = JSON.parse(savedData)
        if (memory.emailOrUsername) {
          form.setFieldsValue({ email: memory.emailOrUsername })
        }
      } catch (error) {
        console.error('Failed to load login memory:', error)
      }
    }
  }, [])

  // 保存到记忆
  const saveToMemory = (emailOrUsername: string) => {
    const memory: LoginMemory = {
      emailOrUsername,
      lastUsed: new Date().toISOString()
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(memory))
  }

  const handleSubmit = async (values: LoginForm) => {
    try {
      // 保存用户名/邮箱到记忆
      saveToMemory(values.email)
      
      await login(values.email, values.password)
      message.success('登录成功')
      navigate('/dashboard')
    } catch (error) {
      // 错误已在store中处理
    }
  }

  const handleWeChatLogin = () => {
    message.info('微信登录功能开发中...')
  }

  return (
    <div className="auth-container">
      <div className="auth-content">
        <div className="auth-header">
          <img src="/logo.svg" alt="慧成长" className="auth-logo" />
          <Title level={2} className="auth-title">
            欢迎回到慧成长
          </Title>
          <Text type="secondary" className="auth-subtitle">
            智能化全家庭教育育儿平台
          </Text>
        </div>

        <Card className="auth-card">
          <Form
            form={form}
            name="login"
            layout="vertical"
            onFinish={handleSubmit}
            size="large"
            autoComplete="off"
          >
            <Form.Item
              name="email"
              label="用户名/邮箱"
              rules={[
                { required: true, message: '请输入用户名或邮箱' },
              ]}
            >
              <Input
                prefix={<UserOutlined />}
                placeholder="请输入用户名或邮箱"
                autoComplete="username"
              />
            </Form.Item>

            <Form.Item
              name="password"
              label="密码"
              rules={[
                { required: true, message: '请输入密码' },
                { min: 6, message: '密码至少6位字符' },
              ]}
            >
              <Input.Password
                prefix={<LockOutlined />}
                placeholder="请输入密码"
                autoComplete="current-password"
              />
            </Form.Item>

            <Form.Item>
              <div className="form-actions">
                <Form.Item name="remember" valuePropName="checked" noStyle>
                  <input type="checkbox" id="remember" />
                  <label htmlFor="remember"> 记住我</label>
                </Form.Item>
                <Link to="/forgot-password" className="forgot-link">
                  忘记密码？
                </Link>
              </div>
            </Form.Item>

            <Form.Item>
              <Button
                type="primary"
                htmlType="submit"
                loading={isLoading}
                block
                size="large"
                className="submit-btn"
              >
                登录
              </Button>
            </Form.Item>
          </Form>

          <Divider>或</Divider>

          <Space direction="vertical" style={{ width: '100%' }} size="middle">
            <Button
              icon={<WechatOutlined />}
              size="large"
              block
              onClick={handleWeChatLogin}
              className="wechat-btn"
            >
              微信登录
            </Button>

            <div className="register-link">
              还没有账号？
              <Link to="/register"> 立即注册</Link>
            </div>
          </Space>
        </Card>

        <div className="auth-footer">
          <Text type="secondary" className="footer-text">
            © 2025 慧成长. All rights reserved.
          </Text>
        </div>
      </div>
    </div>
  )
}

export default Login