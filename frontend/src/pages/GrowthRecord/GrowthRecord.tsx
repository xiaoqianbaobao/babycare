import React, { useState, useEffect } from 'react'
import {
  Card,
  Typography,
  Button,
  Row,
  Col,
  Tabs,
  List,
  Avatar,
  Image,
  Modal,
  Form,
  Input,
  Select,
  Upload,
  Tag,
  message,
  Empty,
  Spin,
  Space,
  Pagination,
  Alert,
  Popconfirm
} from 'antd'
import {
  CameraOutlined,
  VideoCameraOutlined,
  EditOutlined,
  TrophyOutlined,
  SoundOutlined,
  PlusOutlined,
  EyeOutlined,
  HeartOutlined,
  UploadOutlined,
  DeleteOutlined
} from '@ant-design/icons'
import type { TabsProps, UploadFile } from 'antd'
import { useAuthStore } from '../../stores/authStore'
import { GrowthRecord as IGrowthRecord } from '../../types'
import { growthRecordAPI } from '../../services/api'

const { Title, Paragraph, Text } = Typography
const { TextArea } = Input
const { Option } = Select

interface CreateRecordModalProps {
  visible: boolean
  onCancel: () => void
  onSubmit: (values: any) => void
  babies: any[]
  loading: boolean
  record?: IGrowthRecord | null
  isEdit?: boolean
}

const CreateRecordModal: React.FC<CreateRecordModalProps> = ({ 
  visible, 
  onCancel, 
  onSubmit, 
  babies, 
  loading,
  record,
  isEdit = false
}) => {
  const [form] = Form.useForm()
  const [fileList, setFileList] = useState<UploadFile[]>([])
  const [recordType, setRecordType] = useState<string>('PHOTO')

  useEffect(() => {
    if (visible && record && isEdit) {
      form.setFieldsValue({
        ...record,
        babyId: parseInt(record.babyId)
      })
      setRecordType(record.type)
      // Set file list for editing
      if (record.mediaUrls) {
        const files = record.mediaUrls.map((url, index) => ({
          uid: `${index}`,
          name: `media-${index}`,
          status: 'done' as const,
          url: url
        }))
        setFileList(files)
      }
    } else if (visible && !isEdit) {
      form.resetFields()
      setFileList([])
      setRecordType('PHOTO')
    }
  }, [visible, record, isEdit, form])

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      const mediaUrls = fileList.map(file => file.url || file.response?.url).filter(Boolean)
      
      await onSubmit({
        ...values,
        babyId: parseInt(values.babyId),
        mediaUrls,
        type: recordType
      })
      
      form.resetFields()
      setFileList([])
      setRecordType('PHOTO')
    } catch (error) {
      console.error('Form validation failed:', error)
    }
  }

  const uploadProps = {
    multiple: true,
    fileList,
    onChange: ({ fileList: newFileList }: { fileList: UploadFile[] }) => {
      setFileList(newFileList)
    },
    beforeUpload: () => false, // Prevent auto upload
  }

  return (
    <Modal
      title={isEdit ? "编辑成长记录" : "创建成长记录"}
      open={visible}
      onCancel={onCancel}
      onOk={babies.length > 0 ? handleSubmit : () => window.location.hash = '#/family-management'}
      confirmLoading={loading}
      okText={babies.length > 0 ? (isEdit ? "更新" : "创建") : "去添加宝宝"}
      width={600}
    >
      <Form form={form} layout="vertical">
        {babies.length > 0 ? (
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
        ) : (
          <Alert
            message="提示"
            description="请先添加宝宝信息，然后才能创建成长记录。点击下方按钮前往家庭管理页面添加宝宝。"
            type="info"
            showIcon
            style={{ marginBottom: '20px' }}
          />
        )}

        <Form.Item label="记录类型">
          <Select value={recordType} onChange={setRecordType} disabled={isEdit}>
            <Option value="PHOTO">📷 照片</Option>
            <Option value="VIDEO">🎬 视频</Option>
            <Option value="DIARY">📝 日记</Option>
            <Option value="MILESTONE">🏆 里程碑</Option>
            <Option value="VOICE">🎤 语音</Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="title"
          label="标题"
          rules={[{ required: true, message: '请输入标题' }]}
        >
          <Input placeholder="记录一个特别的时刻..." />
        </Form.Item>

        <Form.Item name="content" label="内容">
          <TextArea 
            rows={4} 
            placeholder="分享宝宝的成长故事..." 
            maxLength={2000}
            showCount
          />
        </Form.Item>

        {(recordType === 'PHOTO' || recordType === 'VIDEO') && (
          <Form.Item label="上传文件">
            <Upload {...uploadProps}>
              <Button icon={<UploadOutlined />}>选择文件</Button>
            </Upload>
          </Form.Item>
        )}

        <Form.Item name="tags" label="标签">
          <Select
            mode="tags"
            placeholder="添加标签"
            tokenSeparators={[',', ' ']}
            defaultValue={record?.tags || []}
          >
            <Option value="第一次">第一次</Option>
            <Option value="可爱">可爱</Option>
            <Option value="成长">成长</Option>
            <Option value="里程碑">里程碑</Option>
            <Option value="纪念">纪念</Option>
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  )
}

const GrowthRecord: React.FC = () => {
  const { user } = useAuthStore()
  const [records, setRecords] = useState<IGrowthRecord[]>([])
  const [babies, setBabies] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [createModalVisible, setCreateModalVisible] = useState(false)
  const [createLoading, setCreateLoading] = useState(false)
  const [activeTab, setActiveTab] = useState('all')
  const [currentPage, setCurrentPage] = useState(1)
  const [total, setTotal] = useState(0)
  const [showCreateBabyPrompt, setShowCreateBabyPrompt] = useState(false)
  const [editingRecord, setEditingRecord] = useState<IGrowthRecord | null>(null)
  const pageSize = 12

  // 模拟数据
  useEffect(() => {
    loadBabies().then((loadedBabies) => {
      if (loadedBabies && loadedBabies.length > 0) {
        loadRecords(loadedBabies[0].id)
      }
    })
  }, [activeTab, currentPage])

  const loadRecords = async (babyId?: string) => {
    setLoading(true)
    try {
      // 如果没有传入babyId，则使用第一个宝宝的ID
      const targetBabyId = babyId || (babies.length > 0 ? babies[0].id : null)
      
      if (targetBabyId) {
        let response;
        
        if (activeTab !== 'all') {
          // 按类型获取记录
          response = await growthRecordAPI.getRecordsByType(parseInt(targetBabyId), activeTab.toUpperCase());
          const recordsData = response.data || [];
          setRecords(recordsData.map((record: any) => ({
            id: record.id?.toString() || '',
            babyId: record.babyId?.toString() || '',
            type: record.type || 'PHOTO',
            title: record.title || '',
            content: record.content || '',
            mediaUrls: record.mediaUrls || [],
            tags: record.tags || [],
            createdBy: record.createdBy || '',
            createdAt: record.createdAt || new Date().toISOString(),
            updatedAt: record.updatedAt || new Date().toISOString()
          })));
          setTotal(recordsData.length);
        } else {
          // 获取所有记录
          response = await growthRecordAPI.getBabyRecords(parseInt(targetBabyId), currentPage - 1, pageSize);
          const recordsData = response.data?.content || [];
          setRecords(recordsData.map((record: any) => ({
            id: record.id?.toString() || '',
            babyId: record.babyId?.toString() || '',
            type: record.type || 'PHOTO',
            title: record.title || '',
            content: record.content || '',
            mediaUrls: record.mediaUrls || [],
            tags: record.tags || [],
            createdBy: record.createdBy || '',
            createdAt: record.createdAt || new Date().toISOString(),
            updatedAt: record.updatedAt || new Date().toISOString()
          })));
          setTotal(response.data?.totalElements || 0);
        }
      } else {
        // 没有宝宝时显示空数据
        setRecords([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('加载记录失败:', error);
      message.error('加载记录失败');
      // 出错时显示模拟数据
      const mockRecords: IGrowthRecord[] = [
        {
          id: '1',
          babyId: '1',
          type: 'PHOTO',
          title: '宝宝第一次笑',
          content: '今天宝宝第一次对我笑了，太开心了！',
          mediaUrls: ['https://via.placeholder.com/300x200'],
          tags: ['第一次', '可爱'],
          createdBy: '妈妈',
          createdAt: '2024-01-15T10:30:00',
          updatedAt: '2024-01-15T10:30:00'
        },
        {
          id: '2',
          babyId: '1',
          type: 'MILESTONE',
          title: '会坐啦！',
          content: '宝宝今天独立坐了5秒钟，进步很大！',
          mediaUrls: ['https://via.placeholder.com/300x200'],
          tags: ['里程碑', '成长'],
          createdBy: '爸爸',
          createdAt: '2024-01-14T15:20:00',
          updatedAt: '2024-01-14T15:20:00'
        },
        {
          id: '3',
          babyId: '1',
          type: 'DIARY',
          title: '今天的小故事',
          content: '宝宝今天特别乖，一整天都很开心。下午带她去公园玩，看到小鸭子很兴奋。',
          mediaUrls: [],
          tags: ['日常', '公园'],
          createdBy: '妈妈',
          createdAt: '2024-01-13T18:00:00',
          updatedAt: '2024-01-13T18:00:00'
        }
      ]
      
      if (activeTab !== 'all') {
        setRecords(mockRecords.filter(record => record.type === activeTab.toUpperCase()))
      } else {
        setRecords(mockRecords)
      }
      setTotal(mockRecords.length)
    } finally {
      setLoading(false)
    }
  }

  const loadBabies = async () => {
    try {
      // 获取用户的所有家庭和宝宝
      const response = await growthRecordAPI.getBabies();
      const families = response.data || [];
      
      // 提取所有宝宝
      const allBabies: any[] = [];
      families.forEach((family: any) => {
        if (family.babies) {
          family.babies.forEach((baby: any) => {
            allBabies.push({
              id: baby.id?.toString(),
              name: baby.name,
              avatar: baby.avatar
            });
          });
        }
      });
      
      setBabies(allBabies);
      
      // 如果没有宝宝，显示创建宝宝提示
      if (allBabies.length === 0) {
        setShowCreateBabyPrompt(true);
      } else {
        setShowCreateBabyPrompt(false);
      }
      
      return allBabies;
    } catch (error) {
      console.error('加载宝宝信息失败:', error);
      message.error('加载宝宝信息失败');
      // 出错时使用模拟数据
      setBabies([
        { id: '1', name: '小宝', avatar: 'https://via.placeholder.com/40' },
        { id: '2', name: '小贝', avatar: 'https://via.placeholder.com/40' }
      ]);
      setShowCreateBabyPrompt(false);
    }
    return [];
  }

  const handleCreateRecord = async (values: any) => {
    setCreateLoading(true)
    try {
      // 调用实际的API创建成长记录
      await growthRecordAPI.createRecord({
        babyId: values.babyId,
        type: values.type,
        title: values.title,
        content: values.content,
        mediaUrls: values.mediaUrls,
        tags: values.tags
      })
      
      message.success('记录创建成功')
      setCreateModalVisible(false)
      // 重新加载记录
      if (babies.length > 0) {
        loadRecords(babies[0].id)
      }
    } catch (error) {
      console.error('创建记录失败:', error);
      message.error('创建记录失败')
    } finally {
      setCreateLoading(false)
    }
  }

  const handleEditRecord = async (values: any) => {
    setCreateLoading(true)
    try {
      if (!editingRecord) {
        message.error('编辑的记录不存在')
        return
      }
      
      // 调用实际的API更新成长记录
      await growthRecordAPI.updateRecord(parseInt(editingRecord.id), {
        babyId: values.babyId,
        type: values.type,
        title: values.title,
        content: values.content,
        mediaUrls: values.mediaUrls,
        tags: values.tags
      })
      
      message.success('记录更新成功')
      setCreateModalVisible(false)
      setEditingRecord(null)
      // 重新加载记录
      if (babies.length > 0) {
        loadRecords(babies[0].id)
      }
    } catch (error) {
      console.error('编辑记录失败:', error);
      message.error('编辑记录失败')
    } finally {
      setCreateLoading(false)
    }
  }

  const handleDeleteRecord = async (recordId: string) => {
    try {
      // 调用实际的API删除成长记录
      await growthRecordAPI.deleteRecord(parseInt(recordId))
      message.success('记录删除成功')
      // 重新加载记录
      if (babies.length > 0) {
        loadRecords(babies[0].id)
      }
    } catch (error) {
      console.error('删除记录失败:', error);
      message.error('删除记录失败')
    }
  }

  const openEditModal = (record: IGrowthRecord) => {
    setEditingRecord(record)
    setCreateModalVisible(true)
  }

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'PHOTO': return <CameraOutlined style={{ color: '#1890ff' }} />
      case 'VIDEO': return <VideoCameraOutlined style={{ color: '#52c41a' }} />
      case 'DIARY': return <EditOutlined style={{ color: '#faad14' }} />
      case 'MILESTONE': return <TrophyOutlined style={{ color: '#f5222d' }} />
      case 'VOICE': return <SoundOutlined style={{ color: '#722ed1' }} />
      default: return <CameraOutlined />
    }
  }

  const getTypeText = (type: string) => {
    switch (type) {
      case 'PHOTO': return '照片'
      case 'VIDEO': return '视频'
      case 'DIARY': return '日记'
      case 'MILESTONE': return '里程碑'
      case 'VOICE': return '语音'
      default: return '未知'
    }
  }

  const tabItems: TabsProps['items'] = [
    {
      key: 'all',
      label: '全部',
      children: null
    },
    {
      key: 'photo',
      label: (
        <span>
          <CameraOutlined />
          照片
        </span>
      ),
      children: null
    },
    {
      key: 'video',
      label: (
        <span>
          <VideoCameraOutlined />
          视频
        </span>
      ),
      children: null
    },
    {
      key: 'diary',
      label: (
        <span>
          <EditOutlined />
          日记
        </span>
      ),
      children: null
    },
    {
      key: 'milestone',
      label: (
        <span>
          <TrophyOutlined />
          里程碑
        </span>
      ),
      children: null
    }
  ]

  return (
    <div style={{ padding: '24px' }}>
      <Card>
        <div style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <Title level={2} style={{ margin: 0 }}>成长记录</Title>
            <Paragraph type="secondary" style={{ margin: '8px 0 0 0' }}>
              记录宝宝的美好时光，见证每一个成长瞬间
            </Paragraph>
          </div>
          <div>
            {showCreateBabyPrompt ? (
              <Button 
                type="primary" 
                size="large" 
                icon={<PlusOutlined />}
                onClick={() => window.location.hash = '#/family-management'}
              >
                创建宝宝
              </Button>
            ) : (
              <Button 
                type="primary" 
                size="large" 
                icon={<PlusOutlined />}
                onClick={() => {
                  setEditingRecord(null)
                  setCreateModalVisible(true)
                }}
              >
                创建记录
              </Button>
            )}
          </div>
        </div>

        {showCreateBabyPrompt ? (
          <Empty
            description="请先添加宝宝"
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          >
            <Button 
              type="primary" 
              icon={<PlusOutlined />}
              onClick={() => window.location.hash = '#/family-management'}
            >
              去添加宝宝
            </Button>
          </Empty>
        ) : (
          <>
            <Tabs 
              activeKey={activeTab} 
              onChange={setActiveTab}
              items={tabItems}
            />

            <Spin spinning={loading}>
              {records.length === 0 ? (
                <Empty
                  description="还没有成长记录"
                  image={Empty.PRESENTED_IMAGE_SIMPLE}
                >
                  <Button 
                    type="primary" 
                    icon={<PlusOutlined />}
                    onClick={() => {
                      setEditingRecord(null)
                      setCreateModalVisible(true)
                    }}
                  >
                    创建第一条记录
                  </Button>
                </Empty>
              ) : (
                <>
                  <Row gutter={[16, 16]}>
                    {records.map(record => (
                      <Col xs={24} sm={12} md={8} lg={6} key={record.id}>
                        <Card
                          size="small"
                          cover={
                            record.mediaUrls && record.mediaUrls.length > 0 ? (
                              <Image
                                src={record.mediaUrls[0]}
                                alt={record.title}
                                style={{ height: 160, objectFit: 'cover' }}
                              />
                            ) : (
                              <div style={{ 
                                height: 160, 
                                background: '#f5f5f5', 
                                display: 'flex', 
                                alignItems: 'center', 
                                justifyContent: 'center' 
                              }}>
                                {getTypeIcon(record.type)}
                              </div>
                            )
                          }
                          actions={[
                            <Button 
                              type="link" 
                              icon={<EditOutlined />} 
                              key="edit"
                              onClick={() => openEditModal(record)}
                            >
                              编辑
                            </Button>,
                            <Popconfirm
                              title="确认删除"
                              description="确定要删除这条成长记录吗？"
                              onConfirm={() => handleDeleteRecord(record.id)}
                              okText="确认"
                              cancelText="取消"
                              key="delete"
                            >
                              <Button type="link" icon={<DeleteOutlined />} danger>
                                删除
                              </Button>
                            </Popconfirm>,
                            <Space key="view">
                              <EyeOutlined />
                              <Text type="secondary">123</Text>
                            </Space>,
                            <Space key="like">
                              <HeartOutlined />
                              <Text type="secondary">45</Text>
                            </Space>
                          ]}
                        >
                          <Card.Meta
                            title={
                              <Space>
                                {getTypeIcon(record.type)}
                                <Text ellipsis style={{ maxWidth: 120 }}>
                                  {record.title}
                                </Text>
                              </Space>
                            }
                            description={
                              <div>
                                <Paragraph 
                                  ellipsis={{ rows: 2 }} 
                                  style={{ margin: '8px 0', minHeight: 40 }}
                                >
                                  {record.content || '暂无内容'}
                                </Paragraph>
                                <div style={{ marginBottom: '8px' }}>
                                  {record.tags?.map(tag => (
                                    <Tag key={tag}>{tag}</Tag>
                                  ))}
                                </div>
                                <div style={{ 
                                  display: 'flex', 
                                  justifyContent: 'space-between', 
                                  alignItems: 'center',
                                  fontSize: '12px',
                                  color: '#999'
                                }}>
                                  <span>{record.createdBy}</span>
                                  <span>{new Date(record.createdAt).toLocaleDateString()}</span>
                                </div>
                              </div>
                            }
                          />
                        </Card>
                      </Col>
                    ))}
                  </Row>
                  
                  <div style={{ textAlign: 'center', marginTop: '24px' }}>
                    <Pagination
                      current={currentPage}
                      total={total}
                      pageSize={pageSize}
                      onChange={setCurrentPage}
                      showSizeChanger={false}
                      showQuickJumper
                      showTotal={(total, range) => 
                        `第 ${range[0]}-${range[1]} 条，共 ${total} 条记录`
                      }
                    />
                  </div>
                </>
              )}
            </Spin>
          </>
        )}
      </Card>

      <CreateRecordModal
        visible={createModalVisible}
        onCancel={() => {
          setCreateModalVisible(false)
          setEditingRecord(null)
        }}
        onSubmit={editingRecord ? handleEditRecord : handleCreateRecord}
        babies={babies}
        loading={createLoading}
        record={editingRecord}
        isEdit={!!editingRecord}
      />
    </div>
  )
}

export default GrowthRecord