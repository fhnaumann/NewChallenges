export function useTimeable() {

  function formatTime(time: number): string {
    const one_second = 1
    const minute_in_sec = 60 * one_second
    const hour_in_sec = 60 * minute_in_sec
    const day_in_sec = 24 * hour_in_sec

    const timeParts: string[] = []
    let remainingTime = time
    if(remainingTime >= day_in_sec) {
      timeParts.push(`${remainingTime / day_in_sec}d`)
      remainingTime %= day_in_sec
    }
    if(remainingTime >= hour_in_sec) {
      timeParts.push(`${remainingTime / hour_in_sec}h`)
      remainingTime %= hour_in_sec
    }
    if(remainingTime >= minute_in_sec) {
      timeParts.push(`${remainingTime / minute_in_sec}m`)
      remainingTime %= minute_in_sec
    }
    timeParts.push(`${remainingTime}s`)

    return timeParts.join(" ")

  }

  return {
    formatTime
  }
}