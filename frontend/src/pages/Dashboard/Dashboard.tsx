import React from 'react'
import { Row, Col, Card, Statistic, Progress, Avatar, List, Typography, Button, Space, Tag } from 'antd'
import {
  SmileOutlined,
  CameraOutlined,
  BookOutlined,
  TeamOutlined,
  CalendarOutlined,
  TrophyOutlined,
  HeartOutlined,
  RightOutlined,
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useFamilyStore } from '../../stores/familyStore'
import dayjs from 'dayjs'
import './Dashboard.css'

const { Title, Text } = Typography

const Dashboard: React.FC = () => {
  const navigate = useNavigate()
  const { currentBaby, currentFamily } = useFamilyStore()

  // 模拟数据
  const stats = {
    photosCount: 156,
    videosCount: 23,
    diariesCount: 42,
    milestonesCount: 8,
  }

  const recentMilestones = [
    {
      id: '1',
      title: '第一次独立行走',
      date: '2025-08-15',
      category: 'MOTOR',
      achieved: true,
    },
    {
      id: '2',
      title: '会说"妈妈"',
      date: '2025-07-20',
      category: 'LANGUAGE',
      achieved: true,
    },
    {
      id: '3',
      title: '能够独立坐立',
      date: '2025-06-10',
      category: 'MOTOR',
      achieved: true,
    },
  ]

  const developmentProgress = {
    grossMotor: 85,
    fineMotor: 78,
    language: 82,
    cognitive: 90,
    social: 88,
  }

  const recentActivities = [
    {
      id: '1',
      type: 'photo',
      title: '在公园玩耍',
      time: '2小时前',
      author: '妈妈',
    },
    {
      id: '2',
      type: 'diary',
      title: '今天学会了新单词',
      time: '5小时前',
      author: '爸爸',
    },
    {
      id: '3',
      type: 'milestone',
      title: '第一次独立走路',
      time: '1天前',
      author: '奶奶',
    },
  ]

  const upcomingTasks = [
    {
      id: '1',
      title: '疫苗接种提醒',
      dueDate: '明天',
      priority: 'high',
    },
    {
      id: '2',
      title: '早教课程',
      dueDate: '后天',
      priority: 'medium',
    },
    {
      id: '3',
      title: '体检预约',
      dueDate: '下周',
      priority: 'low',
    },
  ]

  const getActivityIcon = (type: string) => {
    switch (type) {
      case 'photo':
        return <CameraOutlined style={{ color: '#52c41a' }} />
      case 'diary':
        return <BookOutlined style={{ color: '#1890ff' }} />
      case 'milestone':
        return <TrophyOutlined style={{ color: '#faad14' }} />
      default:
        return <SmileOutlined />
    }
  }

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'high':
        return 'red'
      case 'medium':
        return 'orange'
      case 'low':
        return 'green'
      default:
        return 'default'
    }
  }

  const calculateAge = (birthday: string) => {
    const birth = dayjs(birthday)
    const now = dayjs()
    const months = now.diff(birth, 'month')
    const years = Math.floor(months / 12)
    const remainingMonths = months % 12

    if (years > 0) {
      return `${years}岁${remainingMonths}个月`
    } else {
      return `${months}个月`
    }
  }

  return (
    <div className="dashboard">
      {/* 宝宝信息卡片 */}
      {currentBaby && (
        <Card className="baby-info-card">
          <div className="baby-info">
            <Avatar size={80} src={currentBaby.avatar} className="baby-avatar">
              {currentBaby.name[0]}
            </Avatar>
            <div className="baby-details">
              <Title level={3} className="baby-name">
                {currentBaby.name}
              </Title>
              <Space direction="vertical" size="small">
                <Text type="secondary">
                  <CalendarOutlined /> {calculateAge(currentBaby.birthday)}
                </Text>
                <Text type="secondary">
                  生日：{dayjs(currentBaby.birthday).format('YYYY年MM月DD日')}
                </Text>
                <Text type="secondary">
                  <TeamOutlined /> {currentFamily?.name}
                </Text>
              </Space>
            </div>
            <div className="baby-actions">
              <Button
                type="primary"
                icon={<CameraOutlined />}
                onClick={() => navigate('/growth-record')}
              >
                记录成长
              </Button>
            </div>
          </div>
        </Card>
      )}

      {/* 统计数据 */}
      <Row gutter={[16, 16]} className="stats-row">
        <Col xs={12} sm={6}>
          <Card>
            <Statistic
              title="照片"
              value={stats.photosCount}
              prefix={<CameraOutlined />}
              valueStyle={{ color: '#3f8600' }}
              suffix="张"
            />
          </Card>
        </Col>
        <Col xs={12} sm={6}>
          <Card>
            <Statistic
              title="视频"
              value={stats.videosCount}
              prefix={<CameraOutlined />}
              valueStyle={{ color: '#cf1322' }}
              suffix="个"
            />
          </Card>
        </Col>
        <Col xs={12} sm={6}>
          <Card>
            <Statistic
              title="日记"
              value={stats.diariesCount}
              prefix={<BookOutlined />}
              valueStyle={{ color: '#1890ff' }}
              suffix="篇"
            />
          </Card>
        </Col>
        <Col xs={12} sm={6}>
          <Card>
            <Statistic
              title="里程碑"
              value={stats.milestonesCount}
              prefix={<TrophyOutlined />}
              valueStyle={{ color: '#faad14' }}
              suffix="个"
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        {/* 发育进度 */}
        <Col xs={24} lg={12}>
          <Card
            title="发育进度"
            extra={
              <Button
                type="link"
                onClick={() => navigate('/ai-parenting')}
                icon={<RightOutlined />}
              >
                查看详情
              </Button>
            }
          >
            <Space direction="vertical" style={{ width: '100%' }} size="middle">
              <div>
                <div className="progress-label">
                  <span>大运动</span>
                  <span>{developmentProgress.grossMotor}%</span>
                </div>
                <Progress percent={developmentProgress.grossMotor} />
              </div>
              <div>
                <div className="progress-label">
                  <span>精细动作</span>
                  <span>{developmentProgress.fineMotor}%</span>
                </div>
                <Progress percent={developmentProgress.fineMotor} />
              </div>
              <div>
                <div className="progress-label">
                  <span>语言发展</span>
                  <span>{developmentProgress.language}%</span>
                </div>
                <Progress percent={developmentProgress.language} />
              </div>
              <div>
                <div className="progress-label">
                  <span>认知能力</span>
                  <span>{developmentProgress.cognitive}%</span>
                </div>
                <Progress percent={developmentProgress.cognitive} />
              </div>
              <div>
                <div className="progress-label">
                  <span>社交情感</span>
                  <span>{developmentProgress.social}%</span>
                </div>
                <Progress percent={developmentProgress.social} />
              </div>
            </Space>
          </Card>
        </Col>

        {/* 最近里程碑 */}
        <Col xs={24} lg={12}>
          <Card
            title="最近里程碑"
            extra={
              <Button
                type="link"
                onClick={() => navigate('/growth-record')}
                icon={<RightOutlined />}
              >
                查看更多
              </Button>
            }
          >
            <List
              dataSource={recentMilestones}
              renderItem={(item) => (
                <List.Item>
                  <List.Item.Meta
                    avatar={
                      <Avatar
                        icon={<TrophyOutlined />}
                        style={{ backgroundColor: '#faad14' }}
                      />
                    }
                    title={item.title}
                    description={dayjs(item.date).format('YYYY年MM月DD日')}
                  />
                  <Tag color="green">已达成</Tag>
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        {/* 最近动态 */}
        <Col xs={24} lg={12}>
          <Card
            title="最近动态"
            extra={
              <Button
                type="link"
                onClick={() => navigate('/family-collaboration')}
                icon={<RightOutlined />}
              >
                查看更多
              </Button>
            }
          >
            <List
              dataSource={recentActivities}
              renderItem={(item) => (
                <List.Item>
                  <List.Item.Meta
                    avatar={getActivityIcon(item.type)}
                    title={item.title}
                    description={
                      <Space>
                        <Text type="secondary">{item.author}</Text>
                        <Text type="secondary">·</Text>
                        <Text type="secondary">{item.time}</Text>
                      </Space>
                    }
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>

        {/* 待办任务 */}
        <Col xs={24} lg={12}>
          <Card
            title="待办任务"
            extra={
              <Button
                type="link"
                onClick={() => navigate('/family-collaboration')}
                icon={<RightOutlined />}
              >
                查看更多
              </Button>
            }
          >
            <List
              dataSource={upcomingTasks}
              renderItem={(item) => (
                <List.Item>
                  <List.Item.Meta
                    title={item.title}
                    description={item.dueDate}
                  />
                  <Tag color={getPriorityColor(item.priority)}>
                    {item.priority === 'high' && '高优先级'}
                    {item.priority === 'medium' && '中优先级'}
                    {item.priority === 'low' && '低优先级'}
                  </Tag>
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>

      {/* 快捷操作 */}
      <Card title="快捷操作" className="quick-actions">
        <Row gutter={[16, 16]}>
          <Col xs={12} sm={6}>
            <Button
              type="dashed"
              size="large"
              block
              icon={<CameraOutlined />}
              onClick={() => navigate('/growth-record')}
              className="quick-btn"
            >
              上传照片
            </Button>
          </Col>
          <Col xs={12} sm={6}>
            <Button
              type="dashed"
              size="large"
              block
              icon={<BookOutlined />}
              onClick={() => navigate('/growth-record')}
              className="quick-btn"
            >
              写日记
            </Button>
          </Col>
          <Col xs={12} sm={6}>
            <Button
              type="dashed"
              size="large"
              block
              icon={<TrophyOutlined />}
              onClick={() => navigate('/growth-record')}
              className="quick-btn"
            >
              记录里程碑
            </Button>
          </Col>
          <Col xs={12} sm={6}>
            <Button
              type="dashed"
              size="large"
              block
              icon={<HeartOutlined />}
              onClick={() => navigate('/ai-parenting')}
              className="quick-btn"
            >
              AI咨询
            </Button>
          </Col>
        </Row>
      </Card>
    </div>
  )
}

export default Dashboard