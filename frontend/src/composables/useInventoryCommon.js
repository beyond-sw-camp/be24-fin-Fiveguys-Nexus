export function useInventoryCommon() {
  const totalStock = (item) => {
    if (typeof item?.count === 'number') return item.count
    return (item?.lots ?? []).reduce((sum, lot) => sum + (lot.qty ?? 0), 0)
  }

  const fifoLots = (item) => {
    const lots = [...(item?.lots ?? [])]
    return lots.sort((a, b) => {
      if (!a.expiry && !b.expiry) return 0
      if (!a.expiry) return 1
      if (!b.expiry) return -1
      return new Date(a.expiry) - new Date(b.expiry)
    })
  }

  const isExpiringSoon = (expiry) => {
    if (!expiry) return false
    const diff = (new Date(expiry) - new Date()) / (1000 * 60 * 60 * 24)
    return diff >= 0 && diff <= 7
  }

  const hasExpiringSoonLot = (item) => {
    return (item?.lots ?? []).some((lot) => isExpiringSoon(lot.expiry))
  }

  return {
    totalStock,
    fifoLots,
    isExpiringSoon,
    hasExpiringSoonLot,
  }
}
