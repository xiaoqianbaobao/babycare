import React, { useState, useEffect } from 'react'
import {
  Card,
  Typography,
  Button,
  Row,
  Col,
  Tabs,
  List,
  Progress,
  Tag,
  Modal,
  Form,
  Input,
  Select,
  DatePicker,
  InputNumber,
  message,
  Empty,
  Spin,
  Space,
  Avatar,
  Divider,
  Timeline,
  Rate,
  Popconfirm
} from 'antd'
import {
  BookOutlined,
  BulbOutlined,
  TrophyOutlined,
  CalendarOutlined,
  PlusOutlined,
  PlayCircleOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  EditOutlined,
  DeleteOutlined
} from '@ant-design/icons'
import type { TabsProps } from 'antd'
import { useAuthStore } from '../../stores/authStore'
import { educationPlanAPI, familyAPI } from '../../services/api'
import dayjs from 'dayjs'

const { Title, Paragraph, Text } = Typography
const { TextArea } = Input
const { Option } = Select
const { RangePicker } = DatePicker

interface EducationPlan {
  id: number
  babyId: number
  babyName: string
  name: string
  description: string
  category: 'COGNITIVE' | 'LANGUAGE' | 'MOTOR' | 'SOCIAL' | 'EMOTIONAL' | 'CREATIVE' | 'MUSIC' | 'ART' | 'READING' | 'MATH'
  status: 'DRAFT' | 'ACTIVE' | 'PAUSED' | 'COMPLETED' | 'CANCELLED'
  startDate: string
  endDate?: string
  targetAgeMonths: number
  difficultyLevel: number
  goals: string
  progressPercentage: number
  createdBy: string
  createdByNickname: string
  totalActivities: number
  completedActivities: number
  pendingActivities: number
  createdAt: string
  updatedAt: string
}

interface EducationActivity {
  id: number
  educationPlanId: number
  educationPlanName: string
  name: string
  description: string
  type: 'READING' | 'GAME' | 'EXERCISE' | 'CRAFT' | 'MUSIC' | 'OUTDOOR' | 'SOCIAL' | 'LEARNING' | 'CREATIVE' | 'ROUTINE'
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'SKIPPED' | 'CANCELLED'
  scheduledTime?: string
  actualStartTime?: string
  actualEndTime?: string
  durationMinutes?: number
  materials?: string
  instructions?: string
  notes?: string
  rating?: number
  completionPercentage: number
  createdAt: string
  updatedAt: string
}

interface Baby {
  id: number
  name: string
  avatar?: string
}

interface CreatePlanModalProps {
  visible: boolean
  onCancel: () => void
  onSubmit: (values: any) => void
  babies: Baby[]
  loading: boolean
  plan?: EducationPlan | null
  isEdit?: boolean
}

const CreatePlanModal: React.FC<CreatePlanModalProps> = ({ 
  visible, 
  onCancel, 
  onSubmit, 
  babies, 
  loading,
  plan,
  isEdit = false
}) => {
  const [form] = Form.useForm()

  useEffect(() => {
    if (visible && plan && isEdit) {
      form.setFieldsValue({
        ...plan,
        startDate: plan.startDate ? dayjs(plan.startDate) : null,
        endDate: plan.endDate ? dayjs(plan.endDate) : null,
        babyId: plan.babyId
      })
    } else if (visible && !isEdit) {
      form.resetFields()
    }
  }, [visible, plan, isEdit, form])

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      await onSubmit(values)
      form.resetFields()
    } catch (error) {
      console.error('Form validation failed:', error)
    }
  }

  return (
    <Modal
      title={isEdit ? "编辑教育计划" : "创建教育计划"}
      open={visible}
      onCancel={onCancel}
      onOk={handleSubmit}
      confirmLoading={loading}
      width={600}
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="babyId"
          label="选择宝宝"
          rules={[{ required: true, message: '请选择宝宝' }]}
        >
          <Select placeholder="请选择宝宝" disabled={isEdit}>
            {babies.map(baby => (
              <Option key={baby.id} value={baby.id}>
                {baby.name}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          name="name"
          label="计划名称"
          rules={[{ required: true, message: '请输入计划名称' }]}
        >
          <Input placeholder="例如：语言启蒙计划" />
        </Form.Item>

        <Form.Item
          name="category"
          label="教育类别"
          rules={[{ required: true, message: '请选择教育类别' }]}
        >
          <Select placeholder="请选择教育类别">
            <Option value="COGNITIVE">🧠 认知发展</Option>
            <Option value="LANGUAGE">🗣️ 语言发展</Option>
            <Option value="MOTOR">🏃 运动发展</Option>
            <Option value="SOCIAL">👥 社交发展</Option>
            <Option value="EMOTIONAL">❤️ 情感发展</Option>
            <Option value="CREATIVE">🎨 创造力</Option>
            <Option value="MUSIC">🎵 音乐启蒙</Option>
            <Option value="ART">🖌️ 艺术启蒙</Option>
            <Option value="READING">📚 阅读启蒙</Option>
            <Option value="MATH">🔢 数学启蒙</Option>
          </Select>
        </Form.Item>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="startDate"
              label="开始日期"
              rules={[{ required: true, message: '请选择开始日期' }]}
            >
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="endDate" label="结束日期">
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="targetAgeMonths"
              label="目标年龄（月）"
              rules={[{ required: true, message: '请输入目标年龄' }]}
            >
              <InputNumber min={1} max={60} style={{ width: '100%' }} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="difficultyLevel" label="难度等级">
              <Select defaultValue={1}>
                <Option value={1}>⭐ 入门</Option>
                <Option value={2}>⭐⭐ 简单</Option>
                <Option value={3}>⭐⭐⭐ 中等</Option>
                <Option value={4}>⭐⭐⭐⭐ 困难</Option>
                <Option value={5}>⭐⭐⭐⭐⭐ 专家</Option>
              </Select>
            </Form.Item>
          </Col>
        </Row>

        <Form.Item name="description" label="计划描述">
          <TextArea 
            rows={3} 
            placeholder="描述这个教育计划的目标和内容..."
            maxLength={2000}
            showCount
          />
        </Form.Item>

        <Form.Item name="goals" label="学习目标">
          <TextArea 
            rows={3} 
            placeholder="设定具体的学习目标..."
            maxLength={2000}
            showCount
          />
        </Form.Item>
      </Form>
    </Modal>
  )
}

const EducationPlanning: React.FC = () => {
  const { user } = useAuthStore()
  const [plans, setPlans] = useState<EducationPlan[]>([])
  const [activities, setActivities] = useState<EducationActivity[]>([])
  const [babies, setBabies] = useState<Baby[]>([])
  const [loading, setLoading] = useState(false)
  const [createModalVisible, setCreateModalVisible] = useState(false)
  const [createLoading, setCreateLoading] = useState(false)
  const [activeTab, setActiveTab] = useState('plans')
  const [selectedPlan, setSelectedPlan] = useState<EducationPlan | null>(null)
  const [editingPlan, setEditingPlan] = useState<EducationPlan | null>(null)

  // 加载数据
  useEffect(() => {
    loadBabies()
  }, [])

  useEffect(() => {
    if (babies.length > 0) {
      loadPlans(babies[0].id)
    }
  }, [babies])

  const loadPlans = async (babyId?: number) => {
    setLoading(true)
    try {
      // 如果没有传入babyId，则使用第一个宝宝的ID
      const targetBabyId = babyId || (babies.length > 0 ? babies[0].id : null)
      
      if (targetBabyId) {
        const response = await educationPlanAPI.getBabyPlans(targetBabyId, 0, 20)
        // 由于API返回的是分页数据，我们需要提取内容
        if (response && response.data && response.data.content) {
          setPlans(response.data.content)
        } else if (response && response.data) {
          setPlans(response.data)
        } else {
          setPlans([])
        }
      }
    } catch (error) {
      console.error('加载教育计划失败:', error)
      message.error('加载教育计划失败')
    } finally {
      setLoading(false)
    }
  }

  const loadBabies = async () => {
    try {
      const response = await familyAPI.getMyFamilies()
      if (response && response.data) {
        // 从家庭数据中提取宝宝信息
        const allBabies: Baby[] = []
        response.data.forEach((family: any) => {
          if (family.babies) {
            family.babies.forEach((baby: any) => {
              allBabies.push({
                id: baby.id,
                name: baby.name,
                avatar: baby.avatar
              })
            })
          }
        })
        setBabies(allBabies)
        return allBabies
      }
    } catch (error) {
      console.error('加载宝宝信息失败:', error)
      message.error('加载宝宝信息失败')
    }
    return []
  }

  const handleCreatePlan = async (values: any) => {
    setCreateLoading(true)
    try {
      // 转换日期格式
      const requestData = {
        ...values,
        startDate: values.startDate ? values.startDate.format('YYYY-MM-DD') : undefined,
        endDate: values.endDate ? values.endDate.format('YYYY-MM-DD') : undefined
      }
      
      await educationPlanAPI.createPlan(requestData)
      message.success('教育计划创建成功')
      setCreateModalVisible(false)
      // 重新加载计划
      if (babies.length > 0) {
        loadPlans(babies[0].id)
      }
    } catch (error) {
      console.error('创建教育计划失败:', error)
      message.error('创建教育计划失败')
    } finally {
      setCreateLoading(false)
    }
  }

  const handleEditPlan = async (values: any) => {
    setCreateLoading(true)
    try {
      if (!editingPlan) {
        message.error('编辑的计划不存在')
        return
      }
      
      // 转换日期格式
      const requestData = {
        ...values,
        startDate: values.startDate ? values.startDate.format('YYYY-MM-DD') : undefined,
        endDate: values.endDate ? values.endDate.format('YYYY-MM-DD') : undefined
      }
      
      await educationPlanAPI.updatePlan(editingPlan.id, requestData)
      message.success('教育计划更新成功')
      setCreateModalVisible(false)
      setEditingPlan(null)
      // 重新加载计划
      if (babies.length > 0) {
        loadPlans(babies[0].id)
      }
    } catch (error) {
      console.error('编辑教育计划失败:', error)
      message.error('编辑教育计划失败')
    } finally {
      setCreateLoading(false)
    }
  }

  const handleDeletePlan = async (planId: number) => {
    try {
      await educationPlanAPI.deletePlan(planId)
      message.success('教育计划删除成功')
      // 重新加载计划
      if (babies.length > 0) {
        loadPlans(babies[0].id)
      }
    } catch (error) {
      console.error('删除教育计划失败:', error)
      message.error('删除教育计划失败')
    }
  }

  const openEditModal = (plan: EducationPlan) => {
    setEditingPlan(plan)
    setCreateModalVisible(true)
  }

  const getCategoryIcon = (category: string) => {
    const icons = {
      COGNITIVE: '🧠',
      LANGUAGE: '🗣️',
      MOTOR: '🏃',
      SOCIAL: '👥',
      EMOTIONAL: '❤️',
      CREATIVE: '🎨',
      MUSIC: '🎵',
      ART: '🖌️',
      READING: '📚',
      MATH: '🔢'
    }
    return icons[category as keyof typeof icons] || '📋'
  }

  const getCategoryText = (category: string) => {
    const texts = {
      COGNITIVE: '认知发展',
      LANGUAGE: '语言发展',
      MOTOR: '运动发展',
      SOCIAL: '社交发展',
      EMOTIONAL: '情感发展',
      CREATIVE: '创造力',
      MUSIC: '音乐启蒙',
      ART: '艺术启蒙',
      READING: '阅读启蒙',
      MATH: '数学启蒙'
    }
    return texts[category as keyof typeof texts] || '未知'
  }

  const getStatusColor = (status: string) => {
    const colors = {
      DRAFT: 'default',
      ACTIVE: 'processing',
      PAUSED: 'warning',
      COMPLETED: 'success',
      CANCELLED: 'error'
    }
    return colors[status as keyof typeof colors] || 'default'
  }

  const getStatusText = (status: string) => {
    const texts = {
      DRAFT: '草稿',
      ACTIVE: '进行中',
      PAUSED: '暂停',
      COMPLETED: '已完成',
      CANCELLED: '已取消'
    }
    return texts[status as keyof typeof texts] || '未知'
  }

  const tabItems: TabsProps['items'] = [
    {
      key: 'plans',
      label: (
        <span>
          <BookOutlined />
          教育计划
        </span>
      ),
      children: (
        <Spin spinning={loading}>
          {plans.length > 0 ? (
            <Row gutter={[16, 16]}>
              {plans.map(plan => (
                <Col xs={24} sm={12} lg={8} key={plan.id}>
                  <Card
                    hoverable
                    actions={[
                      <Button 
                        type="link" 
                        icon={<EditOutlined />} 
                        key="edit"
                        onClick={() => openEditModal(plan)}
                      >
                        编辑
                      </Button>,
                      <Popconfirm
                        title="确认删除"
                        description="确定要删除这个教育计划吗？"
                        onConfirm={() => handleDeletePlan(plan.id)}
                        okText="确认"
                        cancelText="取消"
                        key="delete"
                      >
                        <Button type="link" icon={<DeleteOutlined />} danger>
                          删除
                        </Button>
                      </Popconfirm>,
                      plan.status === 'ACTIVE' ? (
                        <Button type="link" icon={<CheckCircleOutlined />} key="complete">
                          完成
                        </Button>
                      ) : (
                        <Button type="link" icon={<PlayCircleOutlined />} key="start">
                          启动
                        </Button>
                      )
                    ]}
                  >
                    <Card.Meta
                      avatar={<div style={{ fontSize: '24px' }}>{getCategoryIcon(plan.category)}</div>}
                      title={
                        <Space>
                          <Text strong>{plan.name}</Text>
                          <Tag color={getStatusColor(plan.status)}>
                            {getStatusText(plan.status)}
                          </Tag>
                        </Space>
                      }
                      description={
                        <div>
                          <Paragraph ellipsis={{ rows: 2 }} style={{ margin: '8px 0' }}>
                            {plan.description}
                          </Paragraph>
                          <div style={{ marginBottom: '12px' }}>
                            <Text type="secondary">进度：</Text>
                            <Progress 
                              percent={plan.progressPercentage} 
                              size="small" 
                              style={{ marginTop: '4px' }}
                            />
                          </div>
                          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', color: '#666' }}>
                            <span>目标年龄: {plan.targetAgeMonths}月</span>
                            <span>活动: {plan.completedActivities}/{plan.totalActivities}</span>
                          </div>
                        </div>
                      }
                    />
                  </Card>
                </Col>
              ))}
            </Row>
          ) : (
            <Empty description="暂无教育计划" />
          )}
        </Spin>
      )
    },
    {
      key: 'activities',
      label: (
        <span>
          <CalendarOutlined />
          今日活动
        </span>
      ),
      children: (
        <Timeline>
          <Timeline.Item color="green" dot={<CheckCircleOutlined />}>
            <div style={{ marginBottom: '8px' }}>
              <Text strong>阅读绘本</Text>
              <Tag color="success" style={{ marginLeft: '8px' }}>已完成</Tag>
            </div>
            <Text type="secondary">09:00 - 09:30 | 语言启蒙计划</Text>
            <div style={{ marginTop: '4px' }}>
              <Rate disabled defaultValue={5} style={{ fontSize: '12px' }} />
            </div>
          </Timeline.Item>
          <Timeline.Item color="blue" dot={<ClockCircleOutlined />}>
            <div style={{ marginBottom: '8px' }}>
              <Text strong>户外运动</Text>
              <Tag color="processing" style={{ marginLeft: '8px' }}>进行中</Tag>
            </div>
            <Text type="secondary">10:00 - 11:00 | 运动发展计划</Text>
          </Timeline.Item>
          <Timeline.Item dot={<ClockCircleOutlined />}>
            <div style={{ marginBottom: '8px' }}>
              <Text strong>音乐游戏</Text>
              <Tag color="default" style={{ marginLeft: '8px' }}>待开始</Tag>
            </div>
            <Text type="secondary">15:00 - 15:30 | 音乐启蒙计划</Text>
          </Timeline.Item>
        </Timeline>
      )
    },
    {
      key: 'statistics',
      label: (
        <span>
          <TrophyOutlined />
          统计分析
        </span>
      ),
      children: (
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '32px', color: '#1890ff', marginBottom: '8px' }}>
                  {plans.length}
                </div>
                <Text type="secondary">总计划数</Text>
              </div>
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '32px', color: '#52c41a', marginBottom: '8px' }}>
                  {plans.filter(p => p.status === 'ACTIVE').length}
                </div>
                <Text type="secondary">进行中</Text>
              </div>
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '32px', color: '#faad14', marginBottom: '8px' }}>
                  {plans.reduce((sum, p) => sum + (p.totalActivities || 0), 0)}
                </div>
                <Text type="secondary">总活动数</Text>
              </div>
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '32px', color: '#f5222d', marginBottom: '8px' }}>
                  {plans.length > 0 ? Math.round(plans.reduce((sum, p) => sum + p.progressPercentage, 0) / plans.length) : 0}%
                </div>
                <Text type="secondary">平均进度</Text>
              </div>
            </Card>
          </Col>
        </Row>
      )
    }
  ]

  return (
    <div style={{ padding: '24px' }}>
      <Card>
        <div style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <Title level={2} style={{ margin: 0 }}>教育规划</Title>
            <Paragraph type="secondary" style={{ margin: '8px 0 0 0' }}>
              科学制定教育计划，陪伴宝宝健康成长
            </Paragraph>
          </div>
          <Button 
            type="primary" 
            size="large" 
            icon={<PlusOutlined />}
            onClick={() => {
              setEditingPlan(null)
              setCreateModalVisible(true)
            }}
          >
            创建计划
          </Button>
        </div>

        <Tabs 
          activeKey={activeTab} 
          onChange={setActiveTab}
          items={tabItems}
        />
      </Card>

      <CreatePlanModal
        visible={createModalVisible}
        onCancel={() => {
          setCreateModalVisible(false)
          setEditingPlan(null)
        }}
        onSubmit={editingPlan ? handleEditPlan : handleCreatePlan}
        babies={babies}
        loading={createLoading}
        plan={editingPlan}
        isEdit={!!editingPlan}
      />
    </div>
  )
}

export default EducationPlanning