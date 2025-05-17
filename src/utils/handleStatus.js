export function handleStatus(arr) {
  return arr.map((item) => {
    if (item.status === 0) {
      item.status = '待通过'
    } else if (item.status === 1) {
      item.status = '已通过'
    } else if (item.status === 2) {
      item.status = '已拒绝'
    } else {
      item.status = '未知状态'
    }
    return item
  })
}
