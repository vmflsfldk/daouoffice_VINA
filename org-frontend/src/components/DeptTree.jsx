// src/components/DeptTree.jsx
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './DeptTree.css';
import { sortDepartments } from '../utils/deptOrder';

export default function DeptTree({ onSelect, selected, onTreeDataLoad }) {
  const [tree, setTree] = useState([]);
  const [expanded, setExpanded] = useState(new Set());

  // 트리 데이터를 한 번만 로드
  useEffect(() => {
    axios.get('/api/org/tree').then(res => {
      setTree(res.data);
      setExpanded(new Set([0, 10]));
      // 트리 데이터를 부모 컴포넌트에 전달
      if (onTreeDataLoad) {
        onTreeDataLoad(res.data);
      }
    });
  }, []); // 의존성 배열을 비워서 마운트 시에만 실행

  // 선택된 부서에 대한 자동 확장을 별도 함수로 분리
  const expandPathToSelected = () => {
    if (!selected || !tree.length) return;

    const findAndExpandPath = (nodes, targetId, path = []) => {
      for (const node of nodes) {
        if (node.id === targetId) {
          // 경로상의 모든 부모 노드를 확장
          setExpanded(prev => {
            const newExpanded = new Set(prev);
            path.forEach(id => newExpanded.add(id));
            return newExpanded;
          });
          return true;
        }

        if (node.children && node.children.length > 0) {
          if (findAndExpandPath(node.children, targetId, [...path, node.id])) {
            return true;
          }
        }
      }
      return false;
    };

    findAndExpandPath(tree, selected);
  };

  // selected가 변경될 때만 실행
  useEffect(() => {
    if (selected && tree.length > 0) {
      expandPathToSelected();
    }
  }, [selected]); // selected만 의존성으로 유지

  const toggle = id => {
    setExpanded(prev => {
      const next = new Set(prev);
      next.has(id) ? next.delete(id) : next.add(id);
      return next;
    });
  };

  const renderNode = (node, parent) => {
    // 세경하이테크 바로 아래 "세경하이테크(0)" 숨기기
    if (
      parent &&
      parent.name.startsWith('(주)세경하이테크') &&
      node.name === parent.name.replace('(주)', '').trim() &&
      node.memberCount === 0
    ) {
      return null;
    }

    // 생산기술개발실(0) 숨기기
    if (node.name === '생산기술개발실' && node.memberCount === 0) {
      return null;
    }

    if (node.name === '제조센터' && node.memberCount === 0) {
      return null;
    }

    if (node.name === '소재' && node.memberCount === 0) {
      return null;
    }

    const hasChildren = node.children?.length > 0;
    const isOpen     = expanded.has(node.id);

    // 모든 자식 노드를 사진 순서로 강제 정렬
    const childrenToRender = hasChildren && isOpen
      ? sortDepartments(node.children, node.name)
      : [];

    return (
      <li key={node.id}>
        <div className={node.id === selected ? 'node-row selected' : 'node-row'}>
          {hasChildren && (
            <span
              className={isOpen ? 'toggle open' : 'toggle'}
              onClick={() => toggle(node.id)}
            />
          )}
          <span
            className="node-label"
            onClick={() => onSelect(node)}
          >
            {node.name} ({node.memberCount})
          </span>
        </div>

        {hasChildren && isOpen && (
          <ul className="nested">
            {childrenToRender.map(child => renderNode(child, node))}
          </ul>
        )}
      </li>
    );
  };

  return (
    <ul className="dept-tree">
      {tree.map(root => renderNode(root, null))}
    </ul>
  );
}