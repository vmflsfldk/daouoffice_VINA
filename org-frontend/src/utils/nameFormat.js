const PREFIX_CLASS_MAP = {
  QV: 'tag-prefix--qv',
  YP: 'tag-prefix--yp'
};

const SUFFIX_CLASS_MAP = {
  EHS: 'tag-suffix--ehs',
  ACCO: 'tag-suffix--acco',
  HR: 'tag-suffix--hr',
  BUSI: 'tag-suffix--busi',
  WARE: 'tag-suffix--ware',
  PUR: 'tag-suffix--pur',
  TRAD: 'tag-suffix--trad',
  IT: 'tag-suffix--it',
  'R&D': 'tag-suffix--rnd',
  PRO: 'tag-suffix--pro',
  QC: 'tag-suffix--qc'
};

export function splitBracketName(rawName = '') {
  if (!rawName) {
    return {
      hasTag: false,
      tag: '',
      text: ''
    };
  }

  const trimmed = String(rawName).trim();
  const match = trimmed.match(/^\[([^\]]+)\]\s*(.*)$/);

  if (!match) {
    return {
      hasTag: false,
      tag: '',
      text: trimmed
    };
  }

  const [, tagContent, rest] = match;
  const normalizedContent = tagContent.trim();

  const [prefixRaw = '', ...suffixParts] = normalizedContent.split('-');
  const prefix = prefixRaw.trim();
  const suffix = suffixParts.join('-').trim();
  const hasTrailingHyphen = normalizedContent.endsWith('-') && !suffix;

  const prefixKey = prefix.toUpperCase();
  const suffixKey = suffix.toUpperCase();

  return {
    hasTag: true,
    tag: `[${normalizedContent}]`,
    text: rest.trim(),
    tagContent: normalizedContent,
    prefix,
    suffix,
    hasTrailingHyphen,
    prefixClass: PREFIX_CLASS_MAP[prefixKey] || 'tag-prefix--default',
    suffixClass: suffix
      ? SUFFIX_CLASS_MAP[suffixKey] || 'tag-suffix--default'
      : 'tag-suffix--default'
  };
}

export { PREFIX_CLASS_MAP, SUFFIX_CLASS_MAP };
