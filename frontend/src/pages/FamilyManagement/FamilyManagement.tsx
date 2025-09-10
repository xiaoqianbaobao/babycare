import React, { useState, useEffect } from 'react'
import { 
  Card, 
  Button, 
  Form, 
  Input, 
  Modal, 
  message, 
  List, 
  Avatar, 
  Tag, 
  Divider,
  Space,
  Typography 
} from 'antd'
import { 
  PlusOutlined, 
  UserAddOutlined, 
  TeamOutlined, 
  UsergroupAddOutlined,
  CopyOutlined 
} from '@ant-design/icons'
import { request } from '../../services/api'

const { Title, Text } = Typography

interface Family {
  id: number
  name: string
  description: string
  inviteCode: string
  memberCount: number
  babyCount: number
  members: FamilyMember[]
  babies: Baby[]
}

interface FamilyMember {
  id: number
  username: string
  nickname: string
  avatar?: string
  role: 'CREATOR' | 'PARENT' | 'GRANDPARENT' | 'RELATIVE'
}

interface Baby {
  id: number
  name: string
  gender: 'MALE' | 'FEMALE'
  birthday: string
  avatar?: string
  ageDescription: string
}

const FamilyManagement: React.FC = () => {
  const [families, setFamilies] = useState<Family[]>([])
  const [loading, setLoading] = useState(false)
  const [createModalVisible, setCreateModalVisible] = useState(false)
  const [joinModalVisible, setJoinModalVisible] = useState(false)
  const [addBabyModalVisible, setAddBabyModalVisible] = useState(false)
  const [selectedFamilyId, setSelectedFamilyId] = useState<number>()
  const [createForm] = Form.useForm()
  const [joinForm] = Form.useForm()
  const [babyForm] = Form.useForm()

  useEffect(() => {
    loadFamilies()
  }, [])

  const loadFamilies = async () => {
    setLoading(true)
    try {
      const response = await request.get('/family/my-families')
      setFamilies(response.data || [])
    } catch (error) {
      message.error('加载家庭列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleCreateFamily = async (values: any) => {
    try {
      await request.post('/family/create', values)
      message.success('家庭创建成功！')
      setCreateModalVisible(false)
      createForm.resetFields()
      loadFamilies()
    } catch (error: any) {
      message.error(error.response?.data?.message || '创建失败')
    }
  }

  const handleJoinFamily = async (values: any) => {
    try {
      await request.post(`/family/join/${values.inviteCode}`)
      message.success('成功加入家庭！')
      setJoinModalVisible(false)
      joinForm.resetFields()
      loadFamilies()
    } catch (error: any) {
      message.error(error.response?.data?.message || '加入失败')
    }
  }

  const handleAddBaby = async (values: any) => {
    if (!selectedFamilyId) return
    
    try {
      await request.post(`/family/${selectedFamilyId}/babies`, values)
      message.success('宝宝添加成功！')
      setAddBabyModalVisible(false)
      babyForm.resetFields()
      setSelectedFamilyId(undefined)
      loadFamilies()
    } catch (error: any) {
      message.error(error.response?.data?.message || '添加失败')
    }
  }

  const copyInviteCode = (inviteCode: string) => {
    navigator.clipboard.writeText(inviteCode)
    message.success('邀请码已复制到剪贴板')
  }

  const getRoleTag = (role: string) => {
    const roleMap = {
      'CREATOR': { color: 'gold', text: '创建者' },
      'PARENT': { color: 'blue', text: '家长' },
      'GRANDPARENT': { color: 'green', text: '祖父母' },
      'RELATIVE': { color: 'purple', text: '亲属' }
    }
    const config = roleMap[role as keyof typeof roleMap] || { color: 'default', text: role }
    return <Tag color={config.color}>{config.text}</Tag>
  }

  const getGenderIcon = (gender: string) => {
    return gender === 'MALE' ? '👦' : '👧'
  }

  return (
    <div style={{ padding: '24px' }}>
      <div style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Title level={3}>
          <TeamOutlined /> 家庭管理
        </Title>
        <Space>
          <Button 
            type="primary" 
            icon={<PlusOutlined />}
            onClick={() => setCreateModalVisible(true)}
          >
            创建家庭
          </Button>
          <Button 
            icon={<UserAddOutlined />}
            onClick={() => setJoinModalVisible(true)}
          >
            加入家庭
          </Button>
        </Space>
      </div>

      <List
        loading={loading}
        grid={{ gutter: 16, column: 2 }}
        dataSource={families}
        renderItem={(family) => (
          <List.Item>
            <Card
              title={
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span>{family.name}</span>
                  <Space>
                    <Text type="secondary">邀请码: {family.inviteCode}</Text>
                    <Button 
                      size="small" 
                      icon={<CopyOutlined />}
                      onClick={() => copyInviteCode(family.inviteCode)}
                    />
                  </Space>
                </div>
              }
              extra={
                <Button 
                  type="primary" 
                  size="small"
                  icon={<UsergroupAddOutlined />}
                  onClick={() => {
                    setSelectedFamilyId(family.id)
                    setAddBabyModalVisible(true)
                  }}
                >
                  添加宝宝
                </Button>
              }
            >
              {family.description && (
                <Text type="secondary" style={{ display: 'block', marginBottom: '16px' }}>
                  {family.description}
                </Text>
              )}
              
              <div style={{ marginBottom: '16px' }}>
                <Title level={5}>家庭成员 ({family.memberCount})</Title>
                <List
                  size="small"
                  dataSource={family.members}
                  renderItem={(member) => (
                    <List.Item>
                      <List.Item.Meta
                        avatar={<Avatar src={member.avatar}>{member.nickname?.[0]}</Avatar>}
                        title={
                          <Space>
                            {member.nickname}
                            {getRoleTag(member.role)}
                          </Space>
                        }
                        description={`@${member.username}`}
                      />
                    </List.Item>
                  )}
                />
              </div>

              {family.babies.length > 0 && (
                <div>
                  <Title level={5}>宝宝们 ({family.babyCount})</Title>
                  <List
                    size="small"
                    dataSource={family.babies}
                    renderItem={(baby) => (
                      <List.Item>
                        <List.Item.Meta
                          avatar={<Avatar src={baby.avatar}>{getGenderIcon(baby.gender)}</Avatar>}
                          title={baby.name}
                          description={baby.ageDescription}
                        />
                      </List.Item>
                    )}
                  />
                </div>
              )}
            </Card>
          </List.Item>
        )}
      />

      {/* 创建家庭Modal */}
      <Modal
        title="创建家庭"
        open={createModalVisible}
        onCancel={() => setCreateModalVisible(false)}
        footer={null}
      >
        <Form
          form={createForm}
          layout="vertical"
          onFinish={handleCreateFamily}
        >
          <Form.Item
            label="家庭名称"
            name="name"
            rules={[
              { required: true, message: '请输入家庭名称' },
              { min: 2, max: 50, message: '家庭名称长度必须在2-50个字符之间' }
            ]}
          >
            <Input placeholder="请输入家庭名称" />
          </Form.Item>
          
          <Form.Item
            label="家庭描述"
            name="description"
            rules={[
              { max: 200, message: '家庭描述长度不能超过200个字符' }
            ]}
          >
            <Input.TextArea rows={3} placeholder="请输入家庭描述（可选）" />
          </Form.Item>

          <Form.Item>
            <Space style={{ width: '100%', justifyContent: 'flex-end' }}>
              <Button onClick={() => setCreateModalVisible(false)}>
                取消
              </Button>
              <Button type="primary" htmlType="submit">
                创建
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      {/* 加入家庭Modal */}
      <Modal
        title="加入家庭"
        open={joinModalVisible}
        onCancel={() => setJoinModalVisible(false)}
        footer={null}
      >
        <Form
          form={joinForm}
          layout="vertical"
          onFinish={handleJoinFamily}
        >
          <Form.Item
            label="邀请码"
            name="inviteCode"
            rules={[
              { required: true, message: '请输入邀请码' },
              { len: 6, message: '邀请码长度为6位' }
            ]}
          >
            <Input 
              placeholder="请输入6位邀请码" 
              style={{ textTransform: 'uppercase' }}
              maxLength={6}
            />
          </Form.Item>

          <Form.Item>
            <Space style={{ width: '100%', justifyContent: 'flex-end' }}
>
              <Button onClick={() => setJoinModalVisible(false)}>
                取消
              </Button>
              <Button type="primary" htmlType="submit">
                加入
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      {/* 添加宝宝Modal */}
      <Modal
        title="添加宝宝"
        open={addBabyModalVisible}
        onCancel={() => {
          setAddBabyModalVisible(false)
          setSelectedFamilyId(undefined)
        }}
        footer={null}
      >
        <Form
          form={babyForm}
          layout="vertical"
          onFinish={handleAddBaby}
        >
          <Form.Item
            label="宝宝姓名"
            name="name"
            rules={[
              { required: true, message: '请输入宝宝姓名' },
              { min: 1, max: 20, message: '宝宝姓名长度必须在1-20个字符之间' }
            ]}
          >
            <Input placeholder="请输入宝宝姓名" />
          </Form.Item>

          <Form.Item
            label="性别"
            name="gender"
            rules={[{ required: true, message: '请选择宝宝性别' }]}
          >
            <Input.Group>
              <Button.Group>
                <Button 
                  type="default"
                  onClick={() => babyForm.setFieldsValue({ gender: 'MALE' })}
                >
                  👦 男孩
                </Button>
                <Button 
                  type="default"
                  onClick={() => babyForm.setFieldsValue({ gender: 'FEMALE' })}
                >
                  👧 女孩
                </Button>
              </Button.Group>
            </Input.Group>
          </Form.Item>
          
          <Form.Item
            label="生日"
            name="birthday"
            rules={[{ required: true, message: '请选择宝宝生日' }]}
          >
            <Input type="date" />
          </Form.Item>

          <Form.Item>
            <Space style={{ width: '100%', justifyContent: 'flex-end' }}>
              <Button onClick={() => {
                setAddBabyModalVisible(false)
                setSelectedFamilyId(undefined)
              }}>
                取消
              </Button>
              <Button type="primary" htmlType="submit">
                添加
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default FamilyManagement