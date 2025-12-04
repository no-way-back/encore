package com.nowayback.funding.infrastructure.aop;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class CustomSpringELParser {

	private CustomSpringELParser() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
		// Null 체크
		if (key == null || key.trim().isEmpty()) {
			return null;
		}

		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();

		// 파라미터를 컨텍스트에 등록
		if (parameterNames != null && args != null) {
			for (int i = 0; i < parameterNames.length; i++) {
				context.setVariable(parameterNames[i], args[i]);
			}
		}

		try {
			return parser.parseExpression(key).getValue(context, Object.class);
		} catch (Exception e) {
			// SpEL 파싱 실패 시 null 반환
			return null;
		}
	}
}
