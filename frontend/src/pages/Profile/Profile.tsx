import React from 'react'
import { Card, Typography, Button } from 'antd'
import { UserOutlined } from '@ant-design/icons'

const { Title, Paragraph } = Typography

const Profile: React.FC = () => {
  return (
    <div style={{ padding: '24px' }}>
      <Card>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <UserOutlined style={{ fontSize: '64px', color: '#d9d9d9', marginBottom: '16px' }} />
          <Title level={3}>个人资料</Title>
          <Paragraph type="secondary">
            管理个人信息、家庭设置、会员服务等
          </Paragraph>
          <Button type="primary" size="large">
            编辑资料
          </Button>
        </div>
      </Card>
    </div>
  )
}

export default Profile