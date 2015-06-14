package org.zalando.riptide;

/*
 * ⁣​
 * riptide
 * ⁣⁣
 * Copyright (C) 2015 Zalando SE
 * ⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ​⁣
 */

import com.google.common.reflect.TypeToken;
import org.springframework.http.client.ClientHttpResponse;

import javax.annotation.Nullable;
import java.util.Optional;

public final class Retriever {
    
    private final Object value;

    public Retriever(@Nullable Object value) {
        this.value = value;
    }

    public <T> Optional<T> retrieve(Class<T> type) {
        return retrieve(TypeToken.of(type));
    }

    public <T> Optional<T> retrieve(TypeToken<T> type) {
        return Optional.ofNullable(value)
                .filter(v -> type.isAssignableFrom(v.getClass()))
                .map(v -> {
                    @SuppressWarnings("unchecked") 
                    final T t = (T) v;
                    return t;
                });
    }
    
    /**
     * Convenience method for {@code retrieve(ClientHttpResponse.class)}.
     * 
     * @return optional response, present only if successfully captured
     * @see #retrieve(Class) 
     */
    public Optional<ClientHttpResponse> retrieveResponse() {
        return retrieve(ClientHttpResponse.class);
    }

}
