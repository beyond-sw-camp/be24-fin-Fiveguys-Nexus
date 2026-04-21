const categories = [
  '신선육', '튀김·배합', '양념·소스', '사이드·토핑', '포장·소모품', '음료', '장비·세척',
]

const productsByCategory = {
  신선육: ['닭다리살 20kg', '닭봉 10kg', '순살 다짐육 5kg', '가슴살 필렛 2kg'],
  '튀김·배합': ['후라이드 믹스 10kg', '순살 전용믹스 8kg', '양념치킨 코팅가루', '패티믹스 5kg'],
  '양념·소스': ['간장 양념소스 2kg', '양념치킨 소스 2kg', '마늘간장 베이스', '핫스파이시 소스'],
  '사이드·토핑': ['감자튀김 2kg', '치즈볼 1kg', '콜팝 닭껍질', '떡사리 500g'],
  '포장·소모품': ['치킨박스 L', '치킨봉투 대', '장갑 100매', '키친타월 4롤'],
  음료: ['콜라 500ml(24)', '사이다 500ml(24)', '생수 500ml(20)', '에이드 베이스 1L'],
  '장비·세척': ['튀김기름 18L', '그릴 클리너', '필터지', '살균세제 4L'],
}

const categoryWeights = [
  ['신선육', 26], ['튀김·배합', 20], ['양념·소스', 16],
  ['사이드·토핑', 12], ['포장·소모품', 10], ['음료', 8], ['장비·세척', 8],
]
const weightTotal = categoryWeights.reduce((s, [, w]) => s + w, 0)

const storeWeights = [17, 16, 14, 13, 12, 11, 9, 8]
const storeWeightTotal = storeWeights.reduce((s, w) => s + w, 0)
const stores = [
  'BBQ 강남역점', 'BBQ 홍대입구점', 'BBQ 여의도역점', 'BBQ 판교테크노밸리점',
  'BBQ 잠실새내점', 'BBQ 부산서면점', 'BBQ 대전둔산점', 'BBQ 수원광교점',
]

function mulberry32(seed) {
  return function () {
    let t = (seed += 0x6d2b79f5)
    t = Math.imul(t ^ (t >>> 15), t | 1)
    t ^= t + Math.imul(t ^ (t >>> 7), t | 61)
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296
  }
}

function pickCategory(rand) {
  let r = rand() * weightTotal
  for (const [c, w] of categoryWeights) {
    if (r < w) return c
    r -= w
  }
  return categoryWeights[categoryWeights.length - 1][0]
}

function pickStore(rand) {
  let r = rand() * storeWeightTotal
  for (let i = 0; i < stores.length; i++) {
    if (r < storeWeights[i]) return stores[i]
    r -= storeWeights[i]
  }
  return stores[stores.length - 1]
}

function qtyForCategory(cat, rand) {
  if (cat === '포장·소모품') return 120 + Math.floor(rand() * 900)
  if (cat === '음료') return 12 + Math.floor(rand() * 96)
  if (cat === '신선육') return 10 + Math.floor(rand() * 55)
  if (cat === '튀김·배합') return 4 + Math.floor(rand() * 22)
  if (cat === '양념·소스') return 6 + Math.floor(rand() * 36)
  if (cat === '사이드·토핑') return 8 + Math.floor(rand() * 40)
  return 2 + Math.floor(rand() * 14)
}

function unitPriceFor(cat, rand) {
  if (cat === '신선육') return 5200 + Math.floor(rand() * 3800)
  if (cat === '튀김·배합') return 11000 + Math.floor(rand() * 9500)
  if (cat === '양념·소스') return 6500 + Math.floor(rand() * 4500)
  if (cat === '사이드·토핑') return 4800 + Math.floor(rand() * 5200)
  if (cat === '포장·소모품') return 35 + Math.floor(rand() * 120)
  if (cat === '음료') return 780 + Math.floor(rand() * 420)
  return 8000 + Math.floor(rand() * 12000)
}

function buildRawOrders() {
  const rand = mulberry32(20260422)
  const list = []
  const anchor = new Date('2026-04-21T12:00:00')
  for (let day = 0; day < 200; day++) {
    const d = new Date(anchor)
    d.setDate(anchor.getDate() - day)
    const dateStr = d.toISOString().slice(0, 10)
    const dow = d.getDay()
    const isPeak = dow === 5 || dow === 6 || dow === 0
    const isHolidayLike = day % 21 === 0
    let linesPerDay = 10 + Math.floor(rand() * 14)
    if (isPeak) linesPerDay += 8 + Math.floor(rand() * 16)
    if (isHolidayLike) linesPerDay += 12 + Math.floor(rand() * 20)
    linesPerDay = Math.min(linesPerDay, 52)
    for (let i = 0; i < linesPerDay; i++) {
      const cat = pickCategory(rand)
      const names = productsByCategory[cat]
      const product = names[Math.floor(rand() * names.length)]
      const store = pickStore(rand)
      let qty = qtyForCategory(cat, rand)
      if (isPeak && (cat === '신선육' || cat === '튀김·배합')) qty += Math.floor(rand() * 18)
      const unitPrice = unitPriceFor(cat, rand)
      const amount = qty * unitPrice
      let abnormalProb = 0.048
      if (cat === '신선육') abnormalProb += 0.055
      if (cat === '튀김·배합' && qty > 18) abnormalProb += 0.028
      if (qty > 200) abnormalProb += 0.035
      if (isHolidayLike && rand() < 0.15) abnormalProb += 0.04
      const abnormal = rand() < abnormalProb ? 1 : 0
      list.push({ date: dateStr, category: cat, product, store, amount, qty, abnormal })
    }
  }
  return list
}

export const rawOrders = buildRawOrders()
export { categories, stores }
