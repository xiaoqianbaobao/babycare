import React, { useState, useEffect } from 'react'
import {
  Card,
  Typography,
  Button,
  Input,
  List,
  Avatar,
  Space,
  Tabs,
  message
} from 'antd'
import {
  RobotOutlined,
  SendOutlined,
  MessageOutlined,
  UserOutlined
} from '@ant-design/icons'
import type { TabsProps } from 'antd'

const { Title, Paragraph, Text } = Typography
const { TextArea } = Input

interface ChatMessage {
  id: string
  type: 'USER' | 'AI'
  content: string
  createdAt: string
}

const AIParenting: React.FC = () => {
  const [messages, setMessages] = useState<ChatMessage[]>([])
  const [messageInput, setMessageInput] = useState('')
  const [loading, setLoading] = useState(false)

  // 模拟初始消息
  useEffect(() => {
    setMessages([
      {
        id: '1',
        type: 'AI',
        content: '您好！我是您的AI育儿助手，很高兴为您服务。我可以为您提供关于宝宝睡眠、喂养、发育、教育等方面的专业建议。请告诉我您想了解什么？',
        createdAt: new Date().toISOString()
      }
    ])
  }, [])

  const handleSendMessage = async () => {
    if (!messageInput.trim()) return

    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      type: 'USER',
      content: messageInput,
      createdAt: new Date().toISOString()
    }

    setMessages(prev => [...prev, userMessage])
    setMessageInput('')
    setLoading(true)

    // 模拟AI回复
    setTimeout(() => {
      const aiMessage: ChatMessage = {
        id: (Date.now() + 1).toString(),
        type: 'AI',
        content: generateAIReply(messageInput),
        createdAt: new Date().toISOString()
      }
      setMessages(prev => [...prev, aiMessage])
      setLoading(false)
    }, 1000)
  }

  const generateAIReply = (input: string): string => {
    const message = input.toLowerCase()
    
    if (message.includes('睡眠') || message.includes('睡觉')) {
      return '关于宝宝的睡眠问题，我建议：\n\n1. 建立规律的睡眠时间\n2. 创造舒适的睡眠环境\n3. 睡前进行安静的活动\n4. 避免过度刺激\n\n如果问题持续，建议咨询儿科医生。'
    } else if (message.includes('喂养') || message.includes('吃奶') || message.includes('辅食')) {
      return '关于宝宝的喂养，需要根据月龄来调整：\n\n1. 0-6个月：纯母乳或配方奶\n2. 6个月后：逐步添加辅食\n3. 注意营养均衡\n4. 观察宝宝的反应\n\n具体的喂养计划建议咨询儿科医生制定。'
    } else if (message.includes('发育') || message.includes('成长')) {
      return '宝宝的发育是一个渐进的过程：\n\n1. 每个宝宝的发育节奏都不同\n2. 关注关键的发育里程碑\n3. 提供适当的刺激和环境\n4. 定期进行发育评估\n\n如果您担心宝宝的发育情况，建议进行专业的发育评估。'
    } else {
      return '感谢您的提问！我会尽力为您提供专业的育儿建议。如果您有具体的问题，比如关于宝宝的睡眠、喂养、发育、教育等，请详细描述，我会给出更针对性的建议。'
    }
  }

  const tabItems: TabsProps['items'] = [
    {
      key: 'chat',
      label: (
        <span>
          <MessageOutlined />
          智能咨询
        </span>
      ),
      children: (
        <Card style={{ height: '600px' }}>
          <div style={{ height: '500px', overflow: 'auto', marginBottom: '16px' }}>
            <List
              dataSource={messages}
              renderItem={message => (
                <List.Item style={{ border: 'none', padding: '8px 0' }}>
                  <div 
                    style={{ 
                      width: '100%',
                      display: 'flex',
                      justifyContent: message.type === 'USER' ? 'flex-end' : 'flex-start'
                    }}
                  >
                    <div 
                      style={{
                        maxWidth: '70%',
                        padding: '12px 16px',
                        borderRadius: '12px',
                        backgroundColor: message.type === 'USER' ? '#1890ff' : '#f5f5f5',
                        color: message.type === 'USER' ? 'white' : '#333'
                      }}
                    >
                      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '4px' }}>
                        {message.type === 'USER' ? (
                          <UserOutlined style={{ marginRight: '8px' }} />
                        ) : (
                          <RobotOutlined style={{ marginRight: '8px' }} />
                        )}
                        <Text style={{ fontSize: '12px', opacity: 0.8 }}>
                          {message.type === 'USER' ? '我' : 'AI助手'}
                        </Text>
                      </div>
                      <div style={{ whiteSpace: 'pre-line' }}>
                        {message.content}
                      </div>
                    </div>
                  </div>
                </List.Item>
              )}
            />
          </div>
          
          <div style={{ display: 'flex', gap: '8px' }}>
            <TextArea
              rows={2}
              value={messageInput}
              onChange={(e) => setMessageInput(e.target.value)}
              placeholder="请输入您的问题..."
              onPressEnter={(e) => {
                if (!e.shiftKey) {
                  e.preventDefault()
                  handleSendMessage()
                }
              }}
            />
            <Button 
              type="primary" 
              icon={<SendOutlined />}
              onClick={handleSendMessage}
              loading={loading}
              disabled={!messageInput.trim()}
            >
              发送
            </Button>
          </div>
        </Card>
      )
    }
  ]

  return (
    <div style={{ padding: '24px' }}>
      <Card>
        <div style={{ marginBottom: '24px' }}>
          <Title level={2} style={{ margin: 0 }}>AI育儿助手</Title>
          <Paragraph type="secondary" style={{ margin: '8px 0 0 0' }}>
            24小时在线的智能育儿顾问，为您提供专业的育儿建议和支持
          </Paragraph>
        </div>

        <Tabs 
          defaultActiveKey="chat"
          items={tabItems}
        />
      </Card>
    </div>
  )
}

export default AIParenting