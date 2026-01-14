export const PHOTO_ORDER = [
  '(주)세경하이테크',
  '회장',
  '대표이사',
  '경영지원실',
  '재무팀',
  '인사팀',
  '정보기술팀',
  '사업전략실',
  '글로벌사업부',
  '해외영업팀',
  'I PJT팀',
  '영업팀',
  '구매팀',
  'R&D센터',
  '개발관리팀',
  '디스플레이개발실',
  '광학폴더블개발팀',
  '선행개발팀',
  '제품디바이스개발실',
  '제품개발팀',
  '디자인개발팀',
  '생산기술개발실',
  '신소재개발팀',
  '공정개발팀',
  '제조팀',
  '공정',
  '품질팀',
  '공정',
  'SGDC',
  'SGJ',
  'GDS',
  '성장전략실',
  '성장전략팀',
  'SGV',
  '경영지원팀',
  '영업팀',
  '인사파트',
  '총무파트',
  '영업팀',
  '영업파트',
  '출하창고파트',
  '구매팀',
  '구매파트',
  '자재창고파트',
  '무역파트',
  'EHS파트',
  'IT파트',
  'QV 생산사업부',
  '품질파트',
  '생산파트',
  '개발파트',
  'YP 생산사업부',
  '품질파트',
  '생산파트',
  '생산기술파트',
  '세스맷',
  '고문/감사',
  '기타'
];

export const CHILD_ORDER_MAP = new Map([
  ['성장전략실', ['성장전략팀']],
  [
    'SGV',
    [
      '경영지원팀',
      '회계파트',
      '인사파트',
      '총무파트',
      '영업팀',
      '영업파트',
      '출하창고파트',
      '구매팀',
      '구매파트',
      '자재창고파트',
      '무역파트',
      '인프라지원팀',
      'EHS파트',
      'IT파트',
      'QV 생산사업부',
      '품질파트',
      '생산파트',
      '개발파트',
      'YP 생산사업부',
      '품질파트',
      '생산파트',
      '생산기술파트',
      '세스맷',
      '고문/감사',
      '기타'
    ]
  ],
  ['경영지원팀', ['회계파트', '인사파트', '총무파트']],
  ['영업팀', ['영업파트', '출하창고파트']],
  ['구매팀', ['구매파트', '자재창고파트', '무역파트']],
  ['인프라지원팀', ['EHS파트', 'IT파트']],
  ['QV 생산사업부', ['품질파트', '생산파트', '개발파트']],
  ['YP 생산사업부', ['품질파트', '생산파트', '생산기술파트']]
]);

const resolveIndex = (order, name) => {
  if (!order) {
    return Number.MAX_SAFE_INTEGER;
  }

  const idx = order.indexOf(name);
  return idx === -1 ? Number.MAX_SAFE_INTEGER : idx;
};

export const sortDepartments = (children = [], parentName = '') => {
  const parentSpecificOrder = parentName ? CHILD_ORDER_MAP.get(parentName) : null;

  return [...children].sort((a, b) => {
    const nameA = a?.name ?? '';
    const nameB = b?.name ?? '';

    if (parentSpecificOrder) {
      const parentDiff =
        resolveIndex(parentSpecificOrder, nameA) - resolveIndex(parentSpecificOrder, nameB);
      if (parentDiff !== 0) {
        return parentDiff;
      }
    }

    const defaultDiff = resolveIndex(PHOTO_ORDER, nameA) - resolveIndex(PHOTO_ORDER, nameB);
    if (defaultDiff !== 0) {
      return defaultDiff;
    }

    return nameA.localeCompare(nameB, 'ko');
  });
};
