export function animation(from, to, duration, progress) {
  let v = from
  const start = Date.now()
  const rate = (to - from) / duration
  function step() {
    const time = Date.now() - start
    if (time >= duration) {
      v = to
      progress && progress(v)
      return
    }
    v = from + time * rate
    progress && progress(v)
    requestAnimationFrame(step)
  }
  step()
}
