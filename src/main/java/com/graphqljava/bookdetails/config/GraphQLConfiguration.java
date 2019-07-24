package com.graphqljava.bookdetails.config;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.graphqljava.bookdetails.graphql.BookDataFetcher;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URL;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

/**
 * The config class of {@link GraphQL}
 *
 * @author Tiago Yukio Gashu
 * @version 16/07/2019
 */
@Configuration
public class GraphQLConfiguration {

    @Autowired
    private BookDataFetcher bookDataFetcher;

    @Bean
    public GraphQL graphQL() throws IOException {
        final URL url = Resources.getResource("graphql/schema.graphqls");
        final String sdl = Resources.toString(url, Charsets.UTF_8);
        final GraphQLSchema graphQLSchema = buildSchema(sdl);
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    private GraphQLSchema buildSchema(final String sdl) {
        final TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        final RuntimeWiring runtimeWiring = buildWiring();
        final SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")
                        .dataFetcher("bookById", bookDataFetcher.getBookByIdDataFetcher()))
                .type(newTypeWiring("Book")
                        .dataFetcher("author", bookDataFetcher.getAuthorDataFetcher()))
                .build();
    }

}
