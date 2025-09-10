import React, { useState, useEffect } from 'react'
import {
  Card,
  Typography,
  Button,
  Tabs,
  List,
  Avatar,
  Space,
  Tag,
  Empty,
  Spin,
  Input,
  Form,
  Modal,
  Select,
  DatePicker,
  message,
  Row,
  Col
} from 'antd'
import {
  TeamOutlined,
  MessageOutlined,
  CheckCircleOutlined,
  UserOutlined,
  HeartOutlined,
  CommentOutlined,
  PlusOutlined,
  LikeOutlined
} from '@ant-design/icons'
import type { TabsProps } from 'antd'
import { useAuthStore } from '../../stores/authStore'
import { familyPostAPI, familyTaskAPI, familyAPI } from '../../services/api'

const { Title, Paragraph, Text } = Typography
const { TextArea } = Input
const { Option } = Select

// 定义接口
interface FamilyPost {
  id: number
  familyId: number
  familyName: string
  authorId: number
  authorUsername: string
  authorNickname: string
  content: string
  images?: string[]
  videos?: string[]
  likeCount: number
  commentCount: number
  viewCount: number
  postType: string
  isPinned: boolean
  createdAt: string
  updatedAt: string
}

interface FamilyTask {
  id: number
  familyId: number
  familyName: string
  title: string
  description?: string
  assigneeId: number
  assigneeUsername: string
  assigneeNickname: string
  assignedById: number
  assignedByUsername: string
  assignedByNickname: string
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED' | 'OVERDUE'
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT'
  category: 'FEEDING' | 'DIAPER' | 'BATH' | 'PLAY' | 'EDUCATION' | 'MEDICAL' | 'SLEEP' | 'OTHER'
  dueDate?: string
  completedAt?: string
  completedById?: number
  completedByUsername?: string
  completedByNickname?: string
  completionNotes?: string
  reminderTime?: string
  isRecurring: boolean
  recurrencePattern?: string
  createdAt: string
  updatedAt: string
}

interface CreatePostForm {
  familyId: number
  content: string
  images?: string[]
}

interface CreateTaskForm {
  familyId: number
  title: string
  description?: string
  assigneeId: number
  dueDate?: any
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT'
  category: 'FEEDING' | 'DIAPER' | 'BATH' | 'PLAY' | 'EDUCATION' | 'MEDICAL' | 'SLEEP' | 'OTHER'
  reminderTime?: any
}

interface FamilyMember {
  id: number
  userId: number
  username: string
  nickname: string
  avatar?: string
  role: string
  joinedAt: string
}

interface Family {
  id: number
  name: string
  description?: string
  inviteCode: string
  avatar?: string
  members: FamilyMember[]
  babies: any[]
  memberCount?: number
  babyCount?: number
  createdAt?: string
  updatedAt?: string
}

const FamilyCollaboration: React.FC = () => {
  const { user } = useAuthStore()
  const [activeTab, setActiveTab] = useState('posts')
  const [posts, setPosts] = useState<FamilyPost[]>([])
  const [tasks, setTasks] = useState<FamilyTask[]>([])
  const [loading, setLoading] = useState(false)
  const [createPostModalVisible, setCreatePostModalVisible] = useState(false)
  const [createTaskModalVisible, setCreateTaskModalVisible] = useState(false)
  const [postForm] = Form.useForm()
  const [taskForm] = Form.useForm()
  const [families, setFamilies] = useState<Family[]>([])
  const [selectedFamilyId, setSelectedFamilyId] = useState<number | null>(null)

  // 加载数据
  useEffect(() => {
    loadFamilies()
  }, [])

  useEffect(() => {
    if (selectedFamilyId) {
      if (activeTab === 'posts') {
        loadPosts()
      } else if (activeTab === 'tasks') {
        loadTasks()
      }
    }
  }, [selectedFamilyId, activeTab])

  const loadFamilies = async () => {
    try {
      const response = await familyAPI.getMyFamilies()
      if (response && response.data) {
        setFamilies(response.data)
        // 默认选择第一个家庭
        if (response.data.length > 0) {
          setSelectedFamilyId(response.data[0].id)
        }
      }
    } catch (error) {
      console.error('加载家庭信息失败:', error)
      message.error('加载家庭信息失败')
    }
  }

  const loadPosts = async () => {
    if (!selectedFamilyId) return
    
    setLoading(true)
    try {
      const response = await familyPostAPI.getFamilyPosts(selectedFamilyId, 0, 20)
      if (response && response.data && response.data.content) {
        setPosts(response.data.content)
      } else {
        setPosts([])
      }
    } catch (error) {
      console.error('加载家庭动态失败:', error)
      message.error('加载家庭动态失败')
      setPosts([])
    } finally {
      setLoading(false)
    }
  }

  const loadTasks = async () => {
    if (!selectedFamilyId) return
    
    setLoading(true)
    try {
      const response = await familyTaskAPI.getFamilyTasks(selectedFamilyId, 0, 20)
      if (response && response.data && response.data.content) {
        setTasks(response.data.content)
      } else {
        setTasks([])
      }
    } catch (error) {
      console.error('加载家庭任务失败:', error)
      message.error('加载家庭任务失败')
      setTasks([])
    } finally {
      setLoading(false)
    }
  }

  const handleCreatePost = async (values: CreatePostForm) => {
    try {
      await familyPostAPI.createPost(values)
      message.success('动态发布成功')
      setCreatePostModalVisible(false)
      postForm.resetFields()
      loadPosts()
    } catch (error) {
      console.error('发布动态失败:', error)
      message.error('发布动态失败')
    }
  }

  const handleCreateTask = async (values: CreateTaskForm) => {
    try {
      // 转换日期格式为ISO 8601格式
      const requestData = {
        ...values,
        dueDate: values.dueDate ? values.dueDate.toISOString() : undefined,
        reminderTime: values.reminderTime ? values.reminderTime.toISOString() : undefined
      }
      
      await familyTaskAPI.createTask(requestData)
      message.success('任务创建成功')
      setCreateTaskModalVisible(false)
      taskForm.resetFields()
      loadTasks()
    } catch (error) {
      console.error('创建任务失败:', error)
      message.error('创建任务失败')
    }
  }

  const handleLikePost = async (postId: number) => {
    try {
      await familyPostAPI.likePost(postId)
      // 重新加载数据以更新点赞状态
      loadPosts()
    } catch (error) {
      console.error('点赞失败:', error)
      message.error('点赞失败')
    }
  }

  const getStatusColor = (status: string) => {
    const colors = {
      PENDING: 'default',
      IN_PROGRESS: 'processing',
      COMPLETED: 'success',
      CANCELLED: 'error',
      OVERDUE: 'error'
    }
    return colors[status as keyof typeof colors] || 'default'
  }

  const getStatusText = (status: string) => {
    const texts = {
      PENDING: '待处理',
      IN_PROGRESS: '进行中',
      COMPLETED: '已完成',
      CANCELLED: '已取消',
      OVERDUE: '已过期'
    }
    return texts[status as keyof typeof texts] || '未知'
  }

  const getPriorityColor = (priority: string) => {
    const colors = {
      LOW: 'default',
      MEDIUM: 'orange',
      HIGH: 'red',
      URGENT: 'red'
    }
    return colors[priority as keyof typeof colors] || 'default'
  }

  const getPriorityText = (priority: string) => {
    const texts = {
      LOW: '低',
      MEDIUM: '中',
      HIGH: '高',
      URGENT: '紧急'
    }
    return texts[priority as keyof typeof texts] || '未知'
  }

  const getCategoryText = (category: string) => {
    const texts = {
      FEEDING: '喂养',
      DIAPER: '换尿片',
      BATH: '洗澡',
      PLAY: '游戏',
      EDUCATION: '教育',
      MEDICAL: '医疗',
      SLEEP: '睡眠',
      OTHER: '其他'
    }
    return texts[category as keyof typeof texts] || '未知'
  }

  const CreatePostModal: React.FC = () => (
    <Modal
      title="发布动态"
      open={createPostModalVisible}
      onCancel={() => {
        setCreatePostModalVisible(false)
        postForm.resetFields()
      }}
      onOk={() => postForm.submit()}
      width={600}
    >
      <Form form={postForm} layout="vertical" onFinish={handleCreatePost}>
        <Form.Item
          name="familyId"
          label="选择家庭"
          rules={[{ required: true, message: '请选择家庭' }]}
          initialValue={selectedFamilyId}
        >
          <Select placeholder="请选择家庭">
            {families.map(family => (
              <Option key={family.id} value={family.id}>
                {family.name}
              </Option>
            ))}
          </Select>
        </Form.Item>
        
        <Form.Item
          name="content"
          label="动态内容"
          rules={[{ required: true, message: '请输入动态内容' }]}
        >
          <TextArea 
            rows={4} 
            placeholder="分享宝宝的精彩瞬间..."
            maxLength={1000}
            showCount
          />
        </Form.Item>
      </Form>
    </Modal>
  )

  const CreateTaskModal: React.FC = () => (
    <Modal
      title="创建任务"
      open={createTaskModalVisible}
      onCancel={() => {
        setCreateTaskModalVisible(false)
        taskForm.resetFields()
      }}
      onOk={() => taskForm.submit()}
      width={600}
    >
      <Form form={taskForm} layout="vertical" onFinish={handleCreateTask}>
        <Form.Item
          name="familyId"
          label="选择家庭"
          rules={[{ required: true, message: '请选择家庭' }]}
          initialValue={selectedFamilyId}
        >
          <Select placeholder="请选择家庭">
            {families.map(family => (
              <Option key={family.id} value={family.id}>
                {family.name}
              </Option>
            ))}
          </Select>
        </Form.Item>
        
        <Form.Item
          name="title"
          label="任务标题"
          rules={[{ required: true, message: '请输入任务标题' }]}
        >
          <Input placeholder="例如：给宝宝洗澡" />
        </Form.Item>

        <Form.Item name="description" label="任务描述">
          <TextArea 
            rows={3} 
            placeholder="详细描述任务内容..."
            maxLength={500}
            showCount
          />
        </Form.Item>

        <Form.Item
          name="assigneeId"
          label="分配给"
          rules={[{ required: true, message: '请选择任务负责人' }]}
        >
          <Select placeholder="请选择任务负责人">
            {families.flatMap(family => family.members).map(member => (
              <Option key={member.userId} value={member.userId}>
                {member.nickname || member.username}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item name="category" label="任务类别">
          <Select placeholder="请选择任务类别">
            <Option value="FEEDING">🍼 喂养</Option>
            <Option value="DIAPER">🧻 换尿片</Option>
            <Option value="BATH">🛁 洗澡</Option>
            <Option value="PLAY">🎮 游戏</Option>
            <Option value="EDUCATION">📚 教育</Option>
            <Option value="MEDICAL">🏥 医疗</Option>
            <Option value="SLEEP">😴 睡眠</Option>
            <Option value="OTHER">📝 其他</Option>
          </Select>
        </Form.Item>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name="priority" label="优先级">
              <Select defaultValue="MEDIUM">
                <Option value="LOW">🟢 低</Option>
                <Option value="MEDIUM">🟡 中</Option>
                <Option value="HIGH">🟠 高</Option>
                <Option value="URGENT">🔴 紧急</Option>
              </Select>
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="dueDate" label="截止日期">
              <DatePicker showTime style={{ width: '100%' }} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )

  const tabItems: TabsProps['items'] = [
    {
      key: 'posts',
      label: (
        <span>
          <MessageOutlined />
          家庭动态
        </span>
      ),
      children: (
        <Spin spinning={loading}>
          <div style={{ marginBottom: '16px', textAlign: 'right' }}>
            <Button 
              type="primary" 
              icon={<PlusOutlined />}
              onClick={() => setCreatePostModalVisible(true)}
              disabled={!selectedFamilyId}
            >
              发布动态
            </Button>
          </div>
          
          {posts.length > 0 ? (
            <List
              dataSource={posts}
              renderItem={post => (
                <List.Item>
                  <Card style={{ width: '100%' }}>
                    <List.Item.Meta
                      avatar={<Avatar icon={<UserOutlined />} />}
                      title={
                        <Space>
                          <Text strong>{post.authorNickname || post.authorUsername}</Text>
                          <Text type="secondary" style={{ fontSize: '12px' }}>
                            {new Date(post.createdAt).toLocaleString()}
                          </Text>
                        </Space>
                      }
                      description={
                        <div style={{ marginTop: '8px' }}>
                          <Paragraph>{post.content}</Paragraph>
                          <Space>
                            <Button 
                              type="text" 
                              size="small" 
                              icon={<HeartOutlined />}
                              onClick={() => handleLikePost(post.id)}
                            >
                              {post.likeCount}
                            </Button>
                            <Button type="text" size="small" icon={<CommentOutlined />}>
                              {post.commentCount}
                            </Button>
                            <Text type="secondary" style={{ fontSize: '12px' }}>
                              👁️ {post.viewCount}
                            </Text>
                          </Space>
                        </div>
                      }
                    />
                  </Card>
                </List.Item>
              )}
            />
          ) : (
            <Empty description="暂无家庭动态" />
          )}
        </Spin>
      )
    },
    {
      key: 'tasks',
      label: (
        <span>
          <CheckCircleOutlined />
          家庭任务
        </span>
      ),
      children: (
        <Spin spinning={loading}>
          <div style={{ marginBottom: '16px', textAlign: 'right' }}>
            <Button 
              type="primary" 
              icon={<PlusOutlined />}
              onClick={() => setCreateTaskModalVisible(true)}
              disabled={!selectedFamilyId}
            >
              创建任务
            </Button>
          </div>
          
          {tasks.length > 0 ? (
            <List
              dataSource={tasks}
              renderItem={task => (
                <List.Item>
                  <Card style={{ width: '100%' }}>
                    <Space direction="vertical" size="small" style={{ width: '100%' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Text strong>{task.title}</Text>
                        <Space>
                          <Tag color={getStatusColor(task.status)}>
                            {getStatusText(task.status)}
                          </Tag>
                          <Tag color={getPriorityColor(task.priority)}>
                            {getPriorityText(task.priority)}优先级
                          </Tag>
                          <Tag color="blue">
                            {getCategoryText(task.category)}
                          </Tag>
                        </Space>
                      </div>
                      {task.description && (
                        <Paragraph ellipsis={{ rows: 2 }} style={{ margin: '4px 0' }}>
                          {task.description}
                        </Paragraph>
                      )}
                      <div>
                        <Text type="secondary" style={{ fontSize: '12px' }}>
                          负责人：{task.assigneeNickname || task.assigneeUsername} · 分配者：{task.assignedByNickname || task.assignedByUsername} · 截止日期：{task.dueDate ? new Date(task.dueDate).toLocaleString() : '无'}
                        </Text>
                      </div>
                    </Space>
                  </Card>
                </List.Item>
              )}
            />
          ) : (
            <Empty description="暂无家庭任务" />
          )}
        </Spin>
      )
    }
  ]

  return (
    <div style={{ padding: '24px' }}>
      <Card>
        <div style={{ marginBottom: '24px' }}>
          <Title level={2} style={{ margin: 0 }}>家庭协作</Title>
          <Paragraph type="secondary" style={{ margin: '8px 0 0 0' }}>
            家庭成员互动交流，共同参与宝宝成长的每一个瞬间
          </Paragraph>
        </div>

        {families.length > 0 && (
          <div style={{ marginBottom: '16px' }}>
            <Text strong style={{ marginRight: '8px' }}>选择家庭:</Text>
            <Select 
              value={selectedFamilyId} 
              onChange={setSelectedFamilyId}
              style={{ width: 200 }}
            >
              {families.map(family => (
                <Option key={family.id} value={family.id}>
                  {family.name}
                </Option>
              ))}
            </Select>
          </div>
        )}

        <Tabs 
          activeKey={activeTab} 
          onChange={setActiveTab}
          items={tabItems}
        />
      </Card>

      <CreatePostModal />
      <CreateTaskModal />
    </div>
  )
}

export default FamilyCollaboration